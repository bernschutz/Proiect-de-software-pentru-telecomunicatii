FROM node:20-alpine AS build
WORKDIR /app
COPY package.json package-lock.json* ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html

# Create env.js on start (API_BASE is passed from docker-compose)
# Keep quoting safe for JSON CMD + /bin/sh -c
CMD ["/bin/sh", "-c", "printf \"window.__API_BASE__='%s';\" \"${API_BASE}\" > /usr/share/nginx/html/env.js && nginx -g 'daemon off;'" ]
