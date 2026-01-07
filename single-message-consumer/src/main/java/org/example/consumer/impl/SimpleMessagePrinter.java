package org.example.consumer.impl;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.example.consumer.AbstractSimpleMessagePrinter;

import java.io.PrintStream;

public class SimpleMessagePrinter extends AbstractSimpleMessagePrinter {

    public SimpleMessagePrinter(String serverAddress,
                                String consumerGroup,
                                String topicName,
                                PrintStream targetPrintStream) {
        super(serverAddress, consumerGroup, topicName, targetPrintStream);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // Начало чтения с самого начала
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true"); // Автоматический коммит смещений
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "6000");  // Время ожидания активности от консьюмера
    }
}
