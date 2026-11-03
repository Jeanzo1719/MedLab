# imagen de producción del frontend: compila Angular con la configuración docker y sirve
# los archivos estáticos con nginx sin privilegios
## build stage (same node and pnpm as the development image)
FROM node:24-alpine AS builder

RUN npm install -g pnpm

WORKDIR /workspace

COPY frontend/package.json frontend/pnpm-lock.yaml ./

## aprove dependecies manually is required
RUN pnpm approve-builds @parcel/watcher esbuild lmdb msgpackr-extract

RUN pnpm install

COPY frontend/ .

## the docker configuration points the API and the WebSocket to the page origin
RUN pnpm ng build --configuration docker

## second stage: static files only
FROM nginxinc/nginx-unprivileged:alpine

COPY docker/prod/frontend-nginx.conf /etc/nginx/conf.d/default.conf

COPY --from=builder /workspace/dist/medlab/browser /usr/share/nginx/html

EXPOSE 8080
