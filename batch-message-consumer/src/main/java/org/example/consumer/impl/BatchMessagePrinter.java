package org.example.consumer.impl;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.example.consumer.AbstractSimpleMessagePrinter;

import java.io.PrintStream;

import static org.apache.kafka.clients.consumer.ConsumerConfig.FETCH_MIN_BYTES_CONFIG;

public class BatchMessagePrinter extends AbstractSimpleMessagePrinter {

    public BatchMessagePrinter(String serverAddress,
                               String consumerGroup,
                               String topicName,
                               PrintStream targetPrintStream,
                               int fetchMinBytes) {
        super(serverAddress, consumerGroup, topicName, targetPrintStream);
        if (fetchMinBytes < 0) {
            throw new IllegalArgumentException("Параметр fetchMinBytes не может быть меньше 0");
        }
        this.properties.put(FETCH_MIN_BYTES_CONFIG, fetchMinBytes); // ждём нужного кол-ва байт
        this.properties.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, "3600000"); // ждём 1 час
        this.properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // Начало чтения с самого начала
        this.properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); // Автоматический коммит смещений
        this.properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "6000"); // Время ожидания активности от консьюмера
    }
}
