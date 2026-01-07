package org.example;

import org.example.consumer.impl.SimpleMessagePrinter;

public class Main {
    public static void main(String[] args) {

        if (args == null || args.length < 3) {
            throw new IllegalStateException("Необходимо передать аргументы: адрес брокера, топик, идентификатор группы");
        }

        String serverAddress = args[0];
        String consumerGroup = args[1];
        String topicName = args[2];

        System.out.println("Читаем из " + serverAddress);
        try (var simpleMessagePrinter = new SimpleMessagePrinter(serverAddress,
                consumerGroup,
                topicName,
                System.out)) {
            simpleMessagePrinter.awaitMessages();
        }
    }
}