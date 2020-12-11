# Piper frontend
Piper will be an AI based system, that will be able to propose automated routines suggestions based on real life events.

## Project setup
```
npm install
```

### Compiles and hot-reloads for development
```
npm run serve
```

### Compiles and minifies for production
```
npm run build
```

### Run your unit tests
```
npm run test:unit
```

### Lints and fixes files
```
npm run lint
```

### Customize configuration
See [Configuration Reference](https://cli.vuejs.org/config/).

### Generate self signed certificate for frontend local docker deploy (or any other)
In folder `/service/frontend/nginx` run this command (tested on macOS):
```
openssl req -newkey rsa:2048 -new -nodes -x509 -days 365 -keyout piper.key -out piper.pem
```
then in your browser type `thisisunsafe` if chrome doesn't allow you to view page.
