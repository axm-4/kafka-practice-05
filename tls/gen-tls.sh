# Создадим корневой сертификат (Root CA)
openssl req -new -nodes -x509 -days 365 -newkey rsa:2048 -keyout ca.key -out ca.crt -config ca.cnf

# Создадим файл для хранения сертификата безопасности
# Объединим сертификат CA (ca.crt) и его ключ (ca.key) в один файл ca.pem:
cat ca.crt ca.key > ca.pem

# Создадим приватные ключи и запросы на сертификаты (CSR)

openssl req -new \
    -newkey rsa:2048 \
    -keyout app-consumer-creds/app-consumer.key \
    -out app-consumer-creds/app-consumer.csr \
    -config app-consumer-creds/app-consumer.cnf \
    -nodes

openssl req -new \
    -newkey rsa:2048 \
    -keyout app-producer-creds/app-producer.key \
    -out app-producer-creds/app-producer.csr \
    -config app-producer-creds/app-producer.cnf \
    -nodes

openssl req -new \
    -newkey rsa:2048 \
    -keyout kafka-0-creds/kafka-0.key \
    -out kafka-0-creds/kafka-0.csr \
    -config kafka-0-creds/kafka-0.cnf \
    -nodes

openssl req -new \
    -newkey rsa:2048 \
    -keyout kafka-1-creds/kafka-1.key \
    -out kafka-1-creds/kafka-1.csr \
    -config kafka-1-creds/kafka-1.cnf \
    -nodes
openssl req -new \
    -newkey rsa:2048 \
    -keyout kafka-2-creds/kafka-2.key \
    -out kafka-2-creds/kafka-2.csr \
    -config kafka-2-creds/kafka-2.cnf \
    -nodes

#Создадим сертификаты, подписанные CA

openssl x509 -req \
    -days 3650 \
    -in app-consumer-creds/app-consumer.csr \
    -CA ca.crt \
    -CAkey ca.key \
    -CAcreateserial \
    -out app-consumer-creds/app-consumer.crt \
    -extfile app-consumer-creds/app-consumer.cnf \
    -extensions v3_req

openssl x509 -req \
    -days 3650 \
    -in app-producer-creds/app-producer.csr \
    -CA ca.crt \
    -CAkey ca.key \
    -CAcreateserial \
    -out app-producer-creds/app-producer.crt \
    -extfile app-producer-creds/app-producer.cnf \
    -extensions v3_req

openssl x509 -req \
    -days 3650 \
    -in kafka-0-creds/kafka-0.csr \
    -CA ca.crt \
    -CAkey ca.key \
    -CAcreateserial \
    -out kafka-0-creds/kafka-0.crt \
    -extfile kafka-0-creds/kafka-0.cnf \
    -extensions v3_req

openssl x509 -req \
    -days 3650 \
    -in kafka-1-creds/kafka-1.csr \
    -CA ca.crt \
    -CAkey ca.key \
    -CAcreateserial \
    -out kafka-1-creds/kafka-1.crt \
    -extfile kafka-1-creds/kafka-1.cnf \
    -extensions v3_req

openssl x509 -req \
    -days 3650 \
    -in kafka-2-creds/kafka-2.csr \
    -CA ca.crt \
    -CAkey ca.key \
    -CAcreateserial \
    -out kafka-2-creds/kafka-2.crt \
    -extfile kafka-2-creds/kafka-2.cnf \
    -extensions v3_req

# Создадим PKCS12-хранилища

openssl pkcs12 -export \
    -in app-consumer-creds/app-consumer.crt \
    -inkey app-consumer-creds/app-consumer.key \
    -chain \
    -CAfile ca.pem \
    -name app-consumer \
    -out app-consumer-creds/app-consumer.p12 \
    -password pass:123456

openssl pkcs12 -export \
    -in app-producer-creds/app-producer.crt \
    -inkey app-producer-creds/app-producer.key \
    -chain \
    -CAfile ca.pem \
    -name app-producer \
    -out app-producer-creds/app-producer.p12 \
    -password pass:123456

openssl pkcs12 -export \
    -in kafka-0-creds/kafka-0.crt \
    -inkey kafka-0-creds/kafka-0.key \
    -chain \
    -CAfile ca.pem \
    -name kafka-0 \
    -out kafka-0-creds/kafka-0.p12 \
    -password pass:123456

openssl pkcs12 -export \
    -in kafka-1-creds/kafka-1.crt \
    -inkey kafka-1-creds/kafka-1.key \
    -chain \
    -CAfile ca.pem \
    -name kafka-1 \
    -out kafka-1-creds/kafka-1.p12 \
    -password pass:123456

openssl pkcs12 -export \
    -in kafka-2-creds/kafka-2.crt \
    -inkey kafka-2-creds/kafka-2.key \
    -chain \
    -CAfile ca.pem \
    -name kafka-2 \
    -out kafka-2-creds/kafka-2.p12 \
    -password pass:123456

# Создадим keystore для Kafka

keytool -importkeystore \
    -deststorepass 123456 \
    -destkeystore app-consumer-creds/kafka.app-consumer.keystore.pkcs12 \
    -srckeystore app-consumer-creds/app-consumer.p12 \
    -deststoretype PKCS12  \
    -srcstoretype PKCS12 \
    -noprompt \
    -srcstorepass 123456

keytool -importkeystore \
    -deststorepass 123456 \
    -destkeystore app-producer-creds/kafka.app-producer.keystore.pkcs12 \
    -srckeystore app-producer-creds/app-producer.p12 \
    -deststoretype PKCS12  \
    -srcstoretype PKCS12 \
    -noprompt \
    -srcstorepass 123456

keytool -importkeystore \
    -deststorepass your-password \
    -destkeystore kafka-0-creds/kafka.kafka-0.keystore.pkcs12 \
    -srckeystore kafka-0-creds/kafka-0.p12 \
    -deststoretype PKCS12  \
    -srcstoretype PKCS12 \
    -noprompt \
    -srcstorepass 123456

keytool -importkeystore \
    -deststorepass your-password \
    -destkeystore kafka-1-creds/kafka.kafka-1.keystore.pkcs12 \
    -srckeystore kafka-1-creds/kafka-1.p12 \
    -deststoretype PKCS12  \
    -srcstoretype PKCS12 \
    -noprompt \
    -srcstorepass 123456

keytool -importkeystore \
    -deststorepass your-password \
    -destkeystore kafka-2-creds/kafka.kafka-2.keystore.pkcs12 \
    -srckeystore kafka-2-creds/kafka-2.p12 \
    -deststoretype PKCS12  \
    -srcstoretype PKCS12 \
    -noprompt \
    -srcstorepass 123456

# Создадим truststore для Kafka

keytool -import \
    -file ca.crt \
    -alias ca \
    -keystore app-consumer-creds/kafka.app-consumer.truststore.jks \
    -storepass 123456 \
    -noprompt

keytool -import \
    -file ca.crt \
    -alias ca \
    -keystore app-producer-creds/kafka.app-producer.truststore.jks \
    -storepass 123456 \
    -noprompt

keytool -import \
    -file ca.crt \
    -alias ca \
    -keystore kafka-0-creds/kafka.kafka-0.truststore.jks \
    -storepass 123456 \
    -noprompt

keytool -import \
    -file ca.crt \
    -alias ca \
    -keystore kafka-1-creds/kafka.kafka-1.truststore.jks \
    -storepass 123456 \
    -noprompt

keytool -import \
    -file ca.crt \
    -alias ca \
    -keystore kafka-2-creds/kafka.kafka-2.truststore.jks \
    -storepass 123456 \
    -noprompt

# Сохраним пароли

echo "123456" > app-consumer-creds/app-consumer_sslkey_creds
echo "123456" > app-producer-creds/app-producer_sslkey_creds
echo "123456" > kafka-0-creds/kafka-0_sslkey_creds
echo "123456" > kafka-1-creds/kafka-1_sslkey_creds
echo "123456" > kafka-2-creds/kafka-2_sslkey_creds
echo "123456" > app-consumer-creds/app-consumer_keystore_creds
echo "123456" > app-producer-creds/app-producer_keystore_creds
echo "123456" > kafka-0-creds/kafka-0_keystore_creds
echo "123456" > kafka-1-creds/kafka-1_keystore_creds
echo "123456" > kafka-2-creds/kafka-2_keystore_creds
echo "123456" > app-consumer-creds/app-consumer_truststore_creds
echo "123456" > app-producer-creds/app-producer_truststore_creds
echo "123456" > kafka-0-creds/kafka-0_truststore_creds
echo "123456" > kafka-1-creds/kafka-1_truststore_creds
echo "123456" > kafka-2-creds/kafka-2_truststore_creds

# Импортируем PKCS12 в JKS

keytool -importkeystore \
    -srckeystore app-consumer-creds/app-consumer.p12 \
    -srcstoretype PKCS12 \
    -destkeystore app-consumer-creds/app-consumer.keystore.jks \
    -deststoretype JKS \
    -deststorepass 123456

keytool -importkeystore \
    -srckeystore app-producer-creds/app-producer.p12 \
    -srcstoretype PKCS12 \
    -destkeystore app-producer-creds/app-producer.keystore.jks \
    -deststoretype JKS \
    -deststorepass 123456

keytool -importkeystore \
    -srckeystore kafka-0-creds/kafka-0.p12 \
    -srcstoretype PKCS12 \
    -destkeystore kafka-0-creds/kafka-0.keystore.jks \
    -deststoretype JKS \
    -deststorepass 123456

keytool -importkeystore \
    -srckeystore kafka-1-creds/kafka-1.p12 \
    -srcstoretype PKCS12 \
    -destkeystore kafka-1-creds/kafka-1.keystore.jks \
    -deststoretype JKS \
    -deststorepass 123456

keytool -importkeystore \
    -srckeystore kafka-2-creds/kafka-2.p12 \
    -srcstoretype PKCS12 \
    -destkeystore kafka-2-creds/kafka-2.keystore.jks \
    -deststoretype JKS \
    -deststorepass 123456

# Импортируем CA в Truststore

keytool -import -trustcacerts -file ca.crt \
    -keystore app-consumer-creds/app-consumer.truststore.jks \
    -storepass 123456 -srcstorepass 123456 -noprompt -alias ca

keytool -import -trustcacerts -file ca.crt \
    -keystore app-producer-creds/app-producer.truststore.jks \
    -storepass 123456 -srcstorepass 123456 -noprompt -alias ca

keytool -import -trustcacerts -file ca.crt \
    -keystore kafka-0-creds/kafka-0.truststore.jks \
    -storepass 123456 -srcstorepass 123456 -noprompt -alias ca

keytool -import -trustcacerts -file ca.crt \
    -keystore kafka-1-creds/kafka-1.truststore.jks \
    -storepass 123456 -srcstorepass 123456 -noprompt -alias ca

keytool -import -trustcacerts -file ca.crt \
    -keystore kafka-2-creds/kafka-2.truststore.jks \
    -storepass 123456 -srcstorepass 123456 -noprompt -alias ca