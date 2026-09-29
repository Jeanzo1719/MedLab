# frontend docker file for development
## use same version as local development and for compatibility with pnpm
FROM node:24-alpine

RUN npm install -g pnpm

WORKDIR /workspace

COPY frontend/package.json frontend/pnpm-lock.yaml ./

## aprove dependecies manually is required
RUN pnpm approve-builds @parcel/watcher esbuild lmdb msgpackr-extract

RUN pnpm install

COPY frontend/ .

## Tauri uses PORT 1240 instead 4200 that is it normally used for Angular
EXPOSE 1420

## 0.0.0.0 to be accesed outside the container and --poll is
## used for live reload works normally inside a container
CMD ["pnpm", "ng", "serve", "--host", "0.0.0.0"]
