import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteBufferSerializer;
import org.example.common.SimpleMessage;
import org.example.common.SimpleMessageSerializer;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

public class IntSequenceMessageProducer implements AutoCloseable {
    private final KafkaProducer<String, SimpleMessage> producer;
    private final String targetTopicName;
    private final AtomicInteger counter = new AtomicInteger(1);

    public IntSequenceMessageProducer(String serverAddress, String targetTopicName) {
        this.targetTopicName = targetTopicName;
        if (serverAddress == null) {
            throw new IllegalArgumentException();
        }
        var properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, serverAddress);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteBufferSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, SimpleMessageSerializer.class.getName());
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG, 3);
        this.producer = new KafkaProducer<>(properties);
    }

    public void emitNextMessage() {
        var message = new SimpleMessage("Hello world " + counter.getAndIncrement());
        ProducerRecord<String, SimpleMessage> record = new ProducerRecord<>(targetTopicName, message);
        producer.send(record);
    }

    @Override
    public void close() {
        if (producer != null) {
            producer.close();
        }
    }
}
