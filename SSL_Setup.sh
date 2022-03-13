# Step 1: In this step, I am creating a keystore that is signed by the Certificate of Authority.

openssl pkcs12 -export       \
    -inkey service.key       \
    -in service.cert         \
    -out client.keystore.p12 \
    -name service_key

# Step 2: In this step, my password is 'wombo' -> I wombo you wombo. I created the following

service.key
service.cert
ca.pem


#Step 3: I will now create a truststore

keytool -import  \
    -file ca.pem \
    -alias CA    \
    -keystore client.truststore.jks


#Step 4: I had to create a password which is called 'password'

#Result are the following

A. client.keystore.p12
B. client.truststore.jks