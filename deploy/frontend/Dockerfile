FROM node:22 AS build-stage

ARG REPO_URL
ARG GIT_BRANCH
WORKDIR /app

# Clonar o repositório
RUN apt-get update && apt-get install -y git && \
    git clone --branch ${GIT_BRANCH} --single-branch https://github.com/SQLutions-FATEC/API-4-Semestre-Frontend.git .

RUN npm install
RUN npm run build

# Hostear com o nginx
FROM nginx:stable-alpine
# Executar apenas o build
COPY --from=build-stage /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
