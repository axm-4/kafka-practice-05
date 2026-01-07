package org.example.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.example.common.SimpleMessage;

import java.io.PrintStream;
import java.time.Duration;
import java.util.Objects;

public abstract class AbstractSimpleMessagePrinter extends AbstractSimpleMessageConsumer {
    protected final PrintStream targetPrintStream;

    protected AbstractSimpleMessagePrinter(String serverAddress,
                                           String consumerGroup,
                                           String topicName,
                                           PrintStream targetPrintStream) {
        super(serverAddress, consumerGroup, topicName);
        this.targetPrintStream = Objects.requireNonNull(targetPrintStream);
    }

    @Override
    public void awaitMessages() {
        init();
        while (!Thread.interrupted()) {
            try {
                ConsumerRecords<String, SimpleMessage> records = consumer.poll(Duration.ofMillis(100)); // Получение сообщений
                if (records.isEmpty()) {
                    continue;
                }
                targetPrintStream.printf("Получено %d сообщений\n", records.count());
                for (ConsumerRecord<String, SimpleMessage> record : records) {
                    targetPrintStream.printf("Получено сообщение: key = %s, value = %s, partition = %d, offset = %d%n",
                            record.key(), record.value(), record.partition(), record.offset());
                }
                consumer.commitSync();
            } catch (RuntimeException e) {
                System.out.println("Не удалось получить сообщения из Kafka: " + e);
            }
        }
    }
}
