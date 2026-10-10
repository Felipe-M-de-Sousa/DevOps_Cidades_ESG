#!/usr/bin/env bash
# Uso: scripts/deploy.sh <staging|production>
# Variaveis esperadas: APP_IMAGE (imagem no GHCR) e MONGO_PASSWORD (GitHub Secret)
set -euo pipefail

ENVIRONMENT="${1:?Informe o ambiente: staging ou production}"
ENV_FILE="env/${ENVIRONMENT}.env"
[ -f "$ENV_FILE" ] || { echo "Arquivo $ENV_FILE nao encontrado"; exit 1; }
: "${APP_IMAGE:?Defina APP_IMAGE}"
: "${MONGO_PASSWORD:?Defina MONGO_PASSWORD}"

set -a; source "$ENV_FILE"; set +a
export APP_IMAGE MONGO_PASSWORD

echo ">>> Deploy em '${ENVIRONMENT}' com a imagem ${APP_IMAGE}"
docker compose pull app
docker compose up -d --no-build --remove-orphans

echo ">>> Aguardando a aplicacao ficar saudavel (porta ${APP_PORT})..."
for i in $(seq 1 40); do
  if curl -fsS "http://localhost:${APP_PORT}/actuator/health" | grep -q '"status":"UP"'; then
    echo "Health check OK (tentativa $i)"
    break
  fi
  if [ "$i" -eq 40 ]; then
    echo "Aplicacao nao ficou saudavel a tempo"; docker compose logs --tail=80; exit 1
  fi
  sleep 3
done

echo ">>> Smoke tests"
RESP=$(curl -fsS "http://localhost:${APP_PORT}/api/ambiente")
echo "GET /api/ambiente -> ${RESP}"
echo "$RESP" | grep -q "\"ambiente\":\"${APP_ENV}\"" || { echo "Ambiente retornado diferente de ${APP_ENV}"; exit 1; }
curl -fsS "http://localhost:${APP_PORT}/api/sensores" > /dev/null
echo "GET /api/sensores -> 200 OK"
docker compose ps
echo ">>> Deploy em '${ENVIRONMENT}' concluido com sucesso"
