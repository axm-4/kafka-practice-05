import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.common.SimpleMessageDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {

        if (args == null || args.length < 3) {
            throw new IllegalStateException("Необходимо передать аргументы: адрес брокера, топик, идентификатор группы");
        }

        String serverAddress = args[0];
        String consumerGroup = args[1];
        String topicName = args[2];

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, serverAddress);  // Адрес брокера Kafka
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);        // Уникальный идентификатор группы
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SimpleMessageDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");        // Начало чтения с самого начала
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");           // Автоматический коммит смещений
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "6000");           // Время ожидания активности от консьюмера

        System.out.println("Читаем из " + serverAddress);
        // Чтение сообщений в бесконечном цикле
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(topicName));
            while (true) {
                ConsumerRecords<String, String> records;
                try {
                    records = consumer.poll(Duration.ofMillis(100)); // Получение сообщений
                } catch (RuntimeException e) {
                    System.out.println("Не удалось получить сообщения из Kafka: " + e);
                    continue;
                }
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("Получено сообщение: key = %s, value = %s, partition = %d, offset = %d%n",
                            record.key(), record.value(), record.partition(), record.offset());
                }
            }
        }
    }
}