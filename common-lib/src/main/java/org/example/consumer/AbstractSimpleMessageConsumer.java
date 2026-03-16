package org.example.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.common.SimpleMessage;
import org.example.common.SimpleMessageDeserializer;

import java.util.Collections;
import java.util.Properties;

public abstract class AbstractSimpleMessageConsumer implements AutoCloseable {
    protected KafkaConsumer<String, SimpleMessage> consumer = null;
    protected final Properties properties = new Properties();
    protected final String targetTopicName;
    protected final String serverAddress;

    protected AbstractSimpleMessageConsumer(String serverAddress,
                                            String consumerGroup,
                                            String topicName) {
        if (serverAddress == null || serverAddress.isEmpty()) {
            throw new IllegalArgumentException("Необходимо задать адрес сервера");
        }
        if (consumerGroup == null || consumerGroup.isEmpty()) {
            throw new IllegalArgumentException("Необходимо задать идентификатор consumer group");
        }
        if (topicName == null || topicName.isEmpty()) {
            throw new IllegalArgumentException("Необходимо задать название топика");
        }
        this.targetTopicName = topicName;
        this.serverAddress = serverAddress;
        properties.put("security.protocol", "SSL");
        properties.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "/etc/kafka/secrets/app-consumer-creds/app-consumer.truststore.jks"); // Truststore
        properties.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "123456"); // Truststore password
        properties.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, "/etc/kafka/secrets/app-consumer-creds/app-consumer.keystore.jks"); // Keystore
        properties.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, "123456"); // Keystore password
        properties.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, "123456"); // Key password
        properties.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "https"); // Отключение проверки hostname
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, serverAddress); // Адрес брокера Kafka
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup); // Уникальный идентификатор группы
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SimpleMessageDeserializer.class.getName());
    }

    public abstract void awaitMessages();

    @Override
    public synchronized void close() {
        if (consumer != null) {
            consumer.close();
        }
    }

    protected synchronized void init() {
        if (consumer == null) {
            this.consumer = new KafkaConsumer<>(properties);
            this.consumer.subscribe(Collections.singletonList(targetTopicName));
        }
    }
}
