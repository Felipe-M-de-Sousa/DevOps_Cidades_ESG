const { Document, Packer, Paragraph, TextRun, HeadingLevel, AlignmentType, Table, TableRow, TableCell,
  WidthType, ShadingType, BorderStyle, Footer, PageNumber, PageBreak, LevelFormat } = require("docx");
const fs = require("fs");
const F = "Calibri", C = "Consolas", G = "1E5631";
const h1 = t => new Paragraph({ heading: HeadingLevel.HEADING_1, spacing: { before: 360, after: 160 }, children: [new TextRun({ text: t, bold: true, color: G, font: F })] });
const h2 = t => new Paragraph({ heading: HeadingLevel.HEADING_2, spacing: { before: 240, after: 120 }, children: [new TextRun({ text: t, bold: true, font: F })] });
const p = t => new Paragraph({ spacing: { after: 140, line: 290 }, alignment: AlignmentType.JUSTIFIED, children: [new TextRun({ text: t, font: F, size: 22 })] });
const bl = t => new Paragraph({ numbering: { reference: "b", level: 0 }, spacing: { after: 70 }, children: [new TextRun({ text: t, font: F, size: 22 })] });
const code = lines => new Table({ width: { size: 9400, type: WidthType.DXA }, columnWidths: [9400],
  rows: [new TableRow({ children: [new TableCell({ width: { size: 9400, type: WidthType.DXA }, shading: { type: ShadingType.CLEAR, fill: "1B1B1B" },
    margins: { top: 120, bottom: 120, left: 160, right: 160 },
    children: lines.map(l => new Paragraph({ spacing: { after: 10 }, children: [new TextRun({ text: l || " ", font: C, size: 17, color: "D4F4D4" })] })) })] })] });
const gap = () => new Paragraph({ spacing: { after: 100 }, children: [] });
const print = (titulo) => new Table({ width: { size: 9400, type: WidthType.DXA }, columnWidths: [9400],
  rows: [new TableRow({ height: { value: 2600, rule: "atLeast" }, children: [new TableCell({ width: { size: 9400, type: WidthType.DXA },
    borders: { top: { style: BorderStyle.DASHED, size: 6, color: "888888" }, bottom: { style: BorderStyle.DASHED, size: 6, color: "888888" }, left: { style: BorderStyle.DASHED, size: 6, color: "888888" }, right: { style: BorderStyle.DASHED, size: 6, color: "888888" } },
    margins: { top: 100, bottom: 100, left: 160, right: 160 },
    children: [new Paragraph({ alignment: AlignmentType.CENTER, children: [new TextRun({ text: "[INSERIR PRINT] " + titulo, italics: true, color: "777777", font: F, size: 20 })] })] })] })] });
const tbl = (head, rows, w) => new Table({ width: { size: w.reduce((a, b) => a + b), type: WidthType.DXA }, columnWidths: w,
  rows: [new TableRow({ tableHeader: true, children: head.map((t, i) => new TableCell({ width: { size: w[i], type: WidthType.DXA }, shading: { type: ShadingType.CLEAR, fill: G }, margins: { top: 70, bottom: 70, left: 100, right: 100 }, children: [new Paragraph({ children: [new TextRun({ text: t, bold: true, color: "FFFFFF", font: F, size: 19 })] })] })) }),
    ...rows.map(r => new TableRow({ children: r.map((t, i) => new TableCell({ width: { size: w[i], type: WidthType.DXA }, margins: { top: 70, bottom: 70, left: 100, right: 100 }, children: [new Paragraph({ children: [new TextRun({ text: t, font: F, size: 19 })] })] })) }))] });

const doc = new Document({
  numbering: { config: [{ reference: "b", levels: [{ level: 0, format: LevelFormat.BULLET, text: "•", alignment: AlignmentType.LEFT, style: { paragraph: { indent: { left: 540, hanging: 270 } } } }] }] },
  sections: [{
    properties: { page: { size: { width: 11906, height: 16838 }, margin: { top: 1300, bottom: 1300, left: 1250, right: 1250 } } },
    footers: { default: new Footer({ children: [new Paragraph({ alignment: AlignmentType.CENTER, children: [new TextRun({ text: "Cidades ESG Inteligentes — DevOps  |  Página ", size: 18, font: F }), new TextRun({ children: [PageNumber.CURRENT], size: 18, font: F })] })] }) },
    children: [
      new Paragraph({ spacing: { before: 2200 }, alignment: AlignmentType.CENTER, children: [new TextRun({ text: "FIAP ON — Navegando pelo mundo DevOps", bold: true, size: 26, color: G, font: F })] }),
      new Paragraph({ spacing: { before: 300, after: 200 }, alignment: AlignmentType.CENTER, children: [new TextRun({ text: "Projeto Cidades ESG Inteligentes", bold: true, size: 44, font: F })] }),
      new Paragraph({ spacing: { after: 1200 }, alignment: AlignmentType.CENTER, children: [new TextRun({ text: "EcoSmart Porto Verde — API Java Spring Boot + MongoDB com pipeline CI/CD, Docker e Docker Compose", italics: true, size: 26, font: F })] }),
      new Paragraph({ alignment: AlignmentType.CENTER, spacing: { after: 80 }, children: [new TextRun({ text: "Integrantes:", bold: true, size: 24, font: F })] }),
      new Paragraph({ alignment: AlignmentType.CENTER, children: [new TextRun({ text: "[Nome completo e RM de cada integrante — preencher]", italics: true, size: 22, font: F })] }),
      new Paragraph({ spacing: { before: 400 }, alignment: AlignmentType.CENTER, children: [new TextRun({ text: "Repositório: [link]   |   Pipeline: [link]", size: 22, font: F })] }),
      new Paragraph({ children: [new PageBreak()] }),

      h1("1. Descrição do pipeline"),
      p("Ferramenta utilizada: GitHub Actions. O workflow está em .github/workflows/ci-cd.yml e é disparado em push e pull request na branch main (e manualmente via workflow_dispatch). Os deploys só acontecem em push na main."),
      tbl(["Etapa (job)", "Lógica"], [
        ["1. build-test", "Configura Java 21 com cache do Maven e executa mvn -B verify: compila o projeto e roda os 7 testes automatizados (JUnit 5 + Mockito + MockMvc, sem depender de banco). Publica o relatório de testes e o .jar como artefatos."],
        ["2. docker", "Depende do job 1. Faz o build da imagem com Buildx (cache gha). Em push na main, autentica no GHCR com o GITHUB_TOKEN e publica ghcr.io/<owner>/<repo>:<sha-curto> e :latest. Em pull request, apenas valida o build."],
        ["3. deploy-staging", "Depende do job 2. Usa o environment 'staging': executa scripts/deploy.sh staging (docker compose pull/up com env/staging.env, porta 8081), aguarda o health check e roda smoke tests (/actuator/health, /api/ambiente, /api/sensores)."],
        ["4. deploy-production", "Depende do staging. Usa o environment 'production' (aprovação manual via Required reviewers): mesmo script com env/production.env (porta 8082, banco e projeto Compose próprios)."],
      ], [2000, 7400]),
      gap(),
      p("Lógica de proteção: falha em build ou testes interrompe o pipeline antes de qualquer imagem ser publicada; produção só recebe a mesma imagem (mesmo SHA) que já passou no staging; as senhas vêm de GitHub Secrets por ambiente e nunca ficam no repositório."),
      h2("Fluxo do pipeline"),
      code(["push na main", "   │", "   ▼", "[1 build-test] ──falhou──▶ pipeline para", "   │ ok", "   ▼", "[2 docker: build + push GHCR]", "   │", "   ▼", "[3 deploy-staging + smoke tests]", "   │ ok", "   ▼", "[4 deploy-production  (aprovação manual)]"]),

      h1("2. Docker"),
      h2("Arquitetura"),
      code(["            Host / runner", "  ┌───────────────────────────────────────┐", "  │  rede Docker: backend                 │", "  │                                       │", "  │  ┌────────────┐      ┌─────────────┐  │", "  │  │ app        │ ───▶ │ mongo:7     │  │", "  │  │ Spring Boot│      │ (sem porta  │  │", "  │  │ :8080      │      │  publicada) │  │", "  │  └─────┬──────┘      └──────┬──────┘  │", "  │   vol app-logs        vol mongo-data  │", "  └────────┼──────────────────────────────┘", "           ▼", "   porta APP_PORT (8080 local / 8081 staging / 8082 prod)"]),
      h2("Imagem criada"),
      bl("Dockerfile multi-stage: estágio build (maven:3.9-eclipse-temurin-21) e estágio final eclipse-temurin:21-jre-alpine."),
      bl("Camada de dependências em cache (pom.xml copiado antes do código), usuário não-root, HEALTHCHECK via /actuator/health."),
      bl("Imagem publicada no GHCR: ghcr.io/<owner>/<repo>:<sha> e :latest."),
      h2("Orquestração (docker-compose.yml)"),
      bl("Serviços: mongo (mongo:7) e app (Spring Boot)."),
      bl("Volumes: mongo-data (dados do banco) e app-logs (logs da aplicação)."),
      bl("Rede: backend (bridge); o MongoDB não é exposto ao host."),
      bl("Variáveis de ambiente: via .env / env/*.env (MONGO_USER, MONGO_PASSWORD, MONGO_DB, APP_ENV, APP_PORT, SEED_DATA)."),
      bl("depends_on com condition: service_healthy: a API só inicia após o Mongo responder ao ping."),
      h2("Comandos usados"),
      code(["cp .env.example .env", "docker compose up --build -d", "docker compose ps", "docker compose logs -f app", "curl http://localhost:8080/actuator/health", "curl http://localhost:8080/api/ambiente", "docker compose down        # mantém volumes", "docker compose down -v     # remove volumes"]),

      h1("3. Prints do pipeline rodando"),
      print("Visão geral do workflow com os 4 jobs (GitHub → Actions)"),
      gap(),
      print("Job build-test: log do 'mvn -B verify' com testes passando (Tests run: 7, Failures: 0)"),
      gap(),
      print("Job docker: imagem enviada ao GHCR (aba Packages)"),
      gap(),
      print("Job deploy-staging e deploy-production: logs do deploy.sh com 'Deploy ... concluido com sucesso'"),

      h1("4. Prints dos ambientes staging e produção"),
      print("Staging: curl / navegador em /api/ambiente retornando \"ambiente\":\"staging\" e /api/sensores com dados"),
      gap(),
      print("Produção: /api/ambiente retornando \"ambiente\":\"production\" e /actuator/health UP"),
      gap(),
      print("docker compose ps mostrando mongo e app saudáveis (local ou no runner)"),

      h1("5. Desafios encontrados e como foram resolvidos"),
      p("Os pontos abaixo são os desafios de projeto tratados nesta solução. Ajuste/complemente com o que o grupo realmente enfrentou ao executar o pipeline."),
      tbl(["Desafio", "Solução"], [
        ["A API iniciava antes do MongoDB estar pronto e falhava ao conectar.", "healthcheck no serviço mongo (ping) e depends_on com condition: service_healthy no serviço app."],
        ["Testes dependiam de banco, tornando o pipeline lento e instável.", "Testes de controller com MockMvc standalone e repositórios simulados (Mockito); o banco real é validado nos smoke tests do deploy."],
        ["Duas versões (staging e produção) no mesmo host.", "Um arquivo env/<ambiente>.env por ambiente, com COMPOSE_PROJECT_NAME, porta e banco próprios, evitando conflito de containers, volumes e portas."],
        ["Segredos (senha do banco) não podem ir para o Git.", "Secrets por environment do GitHub; os arquivos versionados têm apenas variáveis não sensíveis; .env no .gitignore e .env.example como modelo."],
        ["GHCR rejeita nomes de imagem com letra maiúscula.", "O nome é convertido para minúsculas no pipeline (${GITHUB_REPOSITORY,,})."],
        ["Garantir que produção rode exatamente o que foi testado.", "A imagem é construída uma vez, versionada pelo SHA do commit, e o mesmo SHA é usado em staging e produção."],
        ["Imagem grande e builds lentos.", "Build multi-stage com JRE alpine e cache de dependências Maven e de camadas Docker (cache gha)."],
      ], [4000, 5400]),

      h1("6. Checklist de entrega"),
      tbl(["Item", "OK"], [
        ["Projeto compactado em .ZIP com estrutura organizada", "☑"],
        ["Dockerfile funcional", "☑"],
        ["docker-compose.yml ou arquivos Kubernetes", "☑"],
        ["Pipeline com etapas de build, teste e deploy", "☑"],
        ["README.md com instruções e prints", "☐ marcar após inserir os prints"],
        ["Documentação técnica com evidências (PDF ou PPT)", "☐ marcar após inserir os prints"],
        ["Deploy realizado nos ambientes staging e produção", "☐ marcar após o pipeline rodar no GitHub"],
      ], [6600, 2800]),
    ],
  }],
});
Packer.toBuffer(doc).then(b => { fs.writeFileSync("/home/claude/cidades-esg-inteligentes/docs/Documentacao_DevOps_Cidades_ESG.docx", b); console.log("ok"); });
