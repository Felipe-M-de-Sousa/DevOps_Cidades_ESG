# ---------- Estagio 1: build (Maven + JDK 21) ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

# Copia so o pom primeiro: a camada de dependencias fica em cache
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q -DskipTests package

# ---------- Estagio 2: runtime (somente JRE, imagem enxuta) ----------
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S ecosmart && adduser -S ecosmart -G ecosmart

WORKDIR /app
RUN mkdir -p /app/logs && chown -R ecosmart:ecosmart /app
COPY --from=build --chown=ecosmart:ecosmart /build/target/ecosmart-api.jar app.jar

USER ecosmart
EXPOSE 8080
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75" \
    LOG_PATH=/app/logs

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
