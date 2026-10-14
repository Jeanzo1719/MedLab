# frontend docker file for development
## use same version as local development and for compatibility with pnpm
FROM node:24-alpine

RUN npm install -g pnpm@12.10.1

WORKDIR /workspace

RUN chown node:node /workspace

USER node

COPY --chown=node:node frontend/package.json frontend/pnpm-lock.yaml frontend/pnpm-workspace.yaml ./

RUN pnpm install --frozen-lockfile

COPY --chown=node:node frontend/ .

## Tauri uses PORT 1240 instead 4200 that is it normally used for Angular
EXPOSE 1420

## 0.0.0.0 to be accesed outside the container
CMD ["pnpm", "ng", "serve", "--host", "0.0.0.0", "--port", "1420"]
