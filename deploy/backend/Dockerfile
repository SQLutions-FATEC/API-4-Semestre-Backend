FROM openjdk:17-jdk-slim AS build

ARG REPO_URL
WORKDIR /app

# Clonar o repositório
RUN apt-get update && apt-get install -y git && \
    git clone --branch ${GIT_BRANCH} --single-branch ${REPO_URL} .

# Buildar a aplicação
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# Executar apenas o compilado .jar
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
