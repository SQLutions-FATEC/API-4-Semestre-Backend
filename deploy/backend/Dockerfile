FROM openjdk:17.0.2-jdk-slim AS build

ARG REPO_URL
ARG BACKEND_BRANCH
WORKDIR /app

# Clonar o repositório
RUN apt-get update && apt-get install -y git && \
    git clone --branch ${BACKEND_BRANCH} --single-branch https://github.com/SQLutions-FATEC/API-4-Semestre-Backend.git .

RUN chmod +x ./mvnw

# Buildar a aplicação
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# Executar apenas o compilado .jar
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
