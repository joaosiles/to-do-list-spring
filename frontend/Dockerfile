# Build Stage
FROM node:lts-alpine as build-stage
WORKDIR /app
COPY package*.json ./

RUN npm ci --ignore-scripts

COPY . .

RUN npx quasar prepare

RUN npx quasar build

FROM nginx:stable-alpine as production-stage
COPY nginx.conf /etc/nginx/nginx.conf
COPY --from=build-stage /app/dist/spa /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]