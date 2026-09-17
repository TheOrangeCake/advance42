FROM node:22-alpine

WORKDIR /app
COPY package.json package-lock.json* ./
RUN npm ci --omit=dev

COPY src ./src

RUN mkdir -p /app/uploads && chown -R node:node /app

ENV NODE_ENV=production

EXPOSE 3000

USER node

CMD ["node", "src/server.js"]
