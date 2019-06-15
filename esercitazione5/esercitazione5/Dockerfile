FROM node:8
WORKDIR /usr/src/app
COPY package*.json ./
RUN npm install
RUN node patch.js
COPY . .
CMD [ "ng", "serve" ]