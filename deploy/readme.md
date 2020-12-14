# Piper deploy
## SSSL certificates
To deploy Piper publicly you need to have a verified SSL certyficate. One can be obtained from Cerbot:
1. Install Certbot and get your certificate:
2. Convert it to `.p12` for `home-service`:
   ```
    sudo openssl pkcs12 -export -name piper -out service/home-service/piper.p12 -in /etc/letsencrypt/live/DOMAIN/fullchain.pem -inkey /etc/letsencrypt/live/DOMAIN/privkey.pem
    sudo chown USER service/home-service/piper.p12
   ```
3. Copy `.pem` and `.key` for frontend:
   ```
    sudo cp /etc/letsencrypt/live/DOMAIN/fullchain.pem piper.pem
    sudo cp /etc/letsencrypt/live/DOMAIN/privkey.pem piper.key
    sudo chown USER piper.pem
    sudo chown USER piper.key
   ```

For local deployment a certificate from local `keygen` works.
