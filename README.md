# Projeto - Cidades ESG Inteligentes (EcoSmart Porto Verde)

API REST em **Java 21 + Spring Boot 3 + MongoDB** para monitoramento de eficiência energética em cidades inteligentes (pilares **E**nvironmental, **S**ocial e **G**overnance), com pipeline **CI/CD no GitHub Actions**, containerização com **Docker** e orquestração com **Docker Compose**.

Evolução do projeto da atividade de NoSQL: as mesmas 5 collections (`sensores_iot`, `leituras_consumo`, `alertas_automaticos`, `unidades_organizacionais`, `conformidade_governanca`) agora são expostas por uma API.

| Collection | Pilar ESG | Endpoint |
|---|---|---|
| sensores_iot | Ambiental | `/api/sensores` |
| leituras_consumo | Ambiental | `/api/leituras` |
| alertas_automaticos | Ambiental / Governança | `/api/alertas` (+ `/api/alertas/abertos`) |
| unidades_organizacionais | Social | `/api/unidades` |
| conformidade_governanca | Governança | `/api/conformidade` |

Outros endpoints: `GET /api/ambiente` (mostra local/staging/production) e `GET /actuator/health`.

## Estrutura

```
cidades-esg-inteligentes/
├── Dockerfile
├── docker-compose.yml
├── .env.example
├── env/                    # variáveis não sensíveis de staging e production
├── scripts/deploy.sh       # deploy + smoke tests (usado pelo pipeline)
├── src/                    # código-fonte e testes
├── docs/                   # documentação técnica e prints
├── README.md
└── .github/workflows/ci-cd.yml
```

## Como executar localmente com Docker

Pré-requisitos: Docker e Docker Compose.

```bash
git clone <URL-DO-SEU-REPOSITORIO>
cd cidades-esg-inteligentes
cp .env.example .env            # ajuste a senha (somente letras e números)
docker compose up --build -d    # sobe MongoDB + aplicação
docker compose ps               # os dois serviços devem estar "healthy"
```

Testando:

```bash
curl http://localhost:8080/actuator/health        # {"status":"UP"}
curl http://localhost:8080/api/ambiente           # {"ambiente":"local",...}
curl http://localhost:8080/api/sensores           # dados de exemplo (SEED_DATA=true)
curl http://localhost:8080/api/alertas/abertos

# criar um sensor (schema flexível: o campo "atributos" aceita qualquer estrutura)
curl -X POST http://localhost:8080/api/sensores -H "Content-Type: application/json" \
  -d '{"tipo":"vazao_agua","status":"ativo","atributos":{"vazao_lmin":12.3}}'
```

Parar: `docker compose down` (mantém os dados) ou `docker compose down -v` (apaga os volumes).

Rodar só os testes, sem Docker: `mvn -B verify` (Java 21 + Maven).

## Pipeline CI/CD

Ferramenta: **GitHub Actions** (`.github/workflows/ci-cd.yml`).

| # | Job | Quando roda | O que faz |
|---|---|---|---|
| 1 | `build-test` | push e pull request | Java 21, `mvn -B verify` (compila e executa os testes JUnit), publica relatório de testes e o `.jar` |
| 2 | `docker` | após o job 1 | Build da imagem; em push na `main`, publica no **GHCR** (`ghcr.io/<owner>/<repo>:<sha>` e `:latest`) |
| 3 | `deploy-staging` | push na `main` | `scripts/deploy.sh staging` (Compose na porta **8081**) + health check + smoke tests |
| 4 | `deploy-production` | após staging OK | `scripts/deploy.sh production` (Compose na porta **8082**) + health check + smoke tests |

Lógica: se o build ou os testes falharem, nada é publicado; produção só é liberada se o staging passar. Cada ambiente usa um *environment* do GitHub, com projeto Compose, banco e porta próprios (`env/staging.env`, `env/production.env`).

Configuração no GitHub (Settings → Environments):
1. Crie os environments `staging` e `production`.
2. Em cada um, crie o secret `MONGO_PASSWORD` (somente letras e números).
3. Em `production`, ative **Required reviewers** para exigir aprovação manual do deploy.
4. (Opcional) Para deploy persistente, registre um *self-hosted runner* com Docker e crie a variável de repositório `DEPLOY_RUNNER` com o label dele. Sem isso, o deploy roda no runner do GitHub: sobe o ambiente completo e valida com smoke tests, mas ele é descartado ao fim do job.

## Containerização

`Dockerfile` (multi-stage):

```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q -DskipTests package

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S ecosmart && adduser -S ecosmart -G ecosmart
WORKDIR /app
RUN mkdir -p /app/logs && chown -R ecosmart:ecosmart /app
COPY --from=build --chown=ecosmart:ecosmart /build/target/ecosmart-api.jar app.jar
USER ecosmart
EXPOSE 8080
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75" LOG_PATH=/app/logs
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health | grep -q '"status":"UP"' || exit 1
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
```

Estratégias adotadas:
- **Multi-stage build**: a imagem final tem só JRE e o `.jar` (sem Maven/JDK).
- **Cache de dependências**: o `pom.xml` é copiado antes do código.
- **Usuário não-root** e **HEALTHCHECK** via Actuator.
- **Compose**: rede `backend` (o MongoDB não expõe porta ao host), volumes `mongo-data` e `app-logs`, variáveis de ambiente via `.env`, `depends_on` com `service_healthy` (a API só sobe depois que o Mongo responde).

## Prints do funcionamento

> Substitua os marcadores abaixo pelos seus prints (salve em `docs/prints/`).

| Evidência | Arquivo |
|---|---|
| Pipeline completo (4 jobs verdes) no GitHub Actions | `docs/prints/01-pipeline.png` |
| Job de build e testes (log do `mvn verify`) | `docs/prints/02-build-testes.png` |
| Imagem publicada no GHCR | `docs/prints/03-ghcr.png` |
| Deploy em staging + `GET /api/ambiente` retornando `staging` | `docs/prints/04-staging.png` |
| Deploy em production + `GET /api/ambiente` retornando `production` | `docs/prints/05-production.png` |
| `docker compose ps` local com serviços healthy | `docs/prints/06-compose-local.png` |

Links (preencha): Repositório: `<link>` · Execução do pipeline: `<link>`

## Tecnologias utilizadas

Java 21 · Spring Boot 3.3 (Web, Data MongoDB, Actuator) · MongoDB 7 · Maven · JUnit 5 + Mockito + MockMvc · Docker (multi-stage) · Docker Compose · GitHub Actions · GitHub Container Registry (GHCR).

## Checklist de entrega

| Item | OK |
|---|---|
| Projeto compactado em .ZIP com estrutura organizada | ☑ |
| Dockerfile funcional | ☑ (validar com `docker compose up --build`) |
| docker-compose.yml ou arquivos Kubernetes | ☑ |
| Pipeline com etapas de build, teste e deploy | ☑ |
| README.md com instruções e prints | ☐ (adicionar os prints) |
| Documentação técnica com evidências (PDF ou PPT) | ☐ (adicionar os prints) |
| Deploy realizado nos ambientes staging e produção | ☐ (marcar após o pipeline rodar no seu GitHub) |
