import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.common.SimpleMessage;
import org.example.common.SimpleMessageDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {

        if (args == null || args.length < 4) {
            throw new IllegalStateException("Необходимо передать аргументы: адрес брокера, топик, идентификатор группы," +
                    "минимальное кол-во байт для считывания");
        }

        String serverAddress = args[0];
        String consumerGroup = args[1];
        String topicName = args[2];
        Integer fetchMinBytes = Integer.parseInt(args[3]);

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, serverAddress); // Адрес брокера Kafka
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup); // Уникальный идентификатор группы
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SimpleMessageDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // Начало чтения с самого начала
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); // Автоматический коммит смещений
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "6000"); // Время ожидания активности от консьюмера
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, fetchMinBytes); // ждём нужного кол-ва байт
        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, "3600000"); // ждём 1 час
        System.out.println("Читаем из " + serverAddress + " пачками минимум по " + fetchMinBytes + " байт");

        // Чтение сообщений в бесконечном цикле
        try (KafkaConsumer<String, SimpleMessage> consumer = new KafkaConsumer<>(props)) {

            consumer.subscribe(Collections.singletonList(topicName));
            while (true) {

                try {
                    ConsumerRecords<String, SimpleMessage> records = consumer.poll(Duration.ofMillis(100)); // Получение сообщений
                    if (records.isEmpty()) {
                        continue;
                    }
                    System.out.printf("Получено %d сообщений\n", records.count());
                    for (ConsumerRecord<String, SimpleMessage> record : records) {
                        System.out.printf("Получено сообщение: key = %s, value = %s, partition = %d, offset = %d%n",
                                record.key(), record.value(), record.partition(), record.offset());
                    }
                    consumer.commitSync();
                } catch (RuntimeException e) {
                    System.out.println("Не удалось получить сообщения из Kafka: " + e);
                }
            }
        }
    }
}