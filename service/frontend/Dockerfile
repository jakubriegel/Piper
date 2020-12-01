FROM node:13.13.0 as setup-stage

WORKDIR /frontend

COPY package.json /frontend/package.json
COPY package-lock.json /frontend/package-lock.json

RUN npm install

COPY .browserslistrc /frontend/.browserslistrc
COPY .eslintrc.js /frontend/.eslintrc.js
COPY .jshintrc /frontend/.jshintrc
COPY babel.config.js /frontend/babel.config.js
COPY jest.config.js /frontend/jest.config.js
COPY vue.config.js /frontend/vue.config.js

COPY public /frontend/public
COPY src /frontend/src

FROM setup-stage as develop-stage

CMD ["npm", "run", "serve"]

FROM setup-stage as install-stage

RUN npm run build

FROM nginx:1.15.7 as production-stage

COPY --from=install-stage /frontend/dist /usr/share/nginx/html

COPY nginx/nginx.conf /etc/nginx/conf.d
COPY nginx/piper.pem /etc/nginx/piper.pem
COPY nginx/piper.key /etc/nginx/piper.key

EXPOSE 8080

CMD ["nginx", "-g", "daemon off;"]
