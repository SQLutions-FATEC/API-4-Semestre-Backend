FROM node:18 AS build-stage

ARG REPO_URL
WORKDIR /app

# Clonar o repositório
RUN apt-get update && apt-get install -y git && \
    git clone --branch ${GIT_BRANCH} --single-branch ${REPO_URL} .

RUN npm install
RUN npm run build

# Hostear com o nginx
FROM nginx:stable-alpine
# Executar apenas o build
COPY --from=build-stage /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
