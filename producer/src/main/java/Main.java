import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteBufferSerializer;
import org.example.common.SimpleMessage;
import org.example.common.SimpleMessageSerializer;

import java.util.Properties;
import java.util.Random;

public class Main {

    public static void main(String[] args) {

        if (args == null || args.length < 2) {
            throw new IllegalStateException("Необходимо передать аргументы: адрес брокера, топик");
        }

        Random random = new Random();

        String serverAddress = args[0];
        String topicName = args[1];

        // Конфигурация продюсера – адрес сервера, сериализаторы для ключа и значения.
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, serverAddress);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteBufferSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, SimpleMessageSerializer.class.getName());
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG, 3);

        System.out.println("Пишем в " + serverAddress + " -> " + topicName);
        try (KafkaProducer<String, SimpleMessage> producer = new KafkaProducer<>(properties)) {
            for (int i = 0; true; i++) {
                var randomMessage = new SimpleMessage("Hello world " + i);
                ProducerRecord<String, SimpleMessage> record = new ProducerRecord<>(topicName, randomMessage);
                producer.send(record);
                System.out.println("Отправлено сообщение: " + record);
                Thread.sleep(random.nextInt(500, 5000));
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
