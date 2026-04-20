# Construção (Build)
FROM node:20-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
# Gera os arquivos de produção do Angular
RUN npm run build --configuration=production

# Servidor Web (NGINX)
FROM nginx:alpine
# Copia os arquivos gerados no Estágio 1 para o servidor
# IMPORTANTE: Verifique se a sua pasta dist gera o arquivo dentro de 'browser'
COPY --from=build /app/dist/pedido-frontend/browser /usr/share/nginx/html

# Expõe a porta padrão do NGINX
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]