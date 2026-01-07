package org.example;

import org.example.consumer.impl.BatchMessagePrinter;

public class Main {
    public static void main(String[] args) {

        if (args == null || args.length < 4) {
            throw new IllegalStateException("Необходимо передать аргументы: адрес брокера, топик, идентификатор группы," +
                    "минимальное кол-во байт для считывания");
        }

        String serverAddress = args[0];
        String consumerGroup = args[1];
        String topicName = args[2];
        int fetchMinBytes = Integer.parseInt(args[3]);

        try (var batchMessagePrinter = new BatchMessagePrinter(serverAddress,
                consumerGroup,
                topicName,
                System.out,
                fetchMinBytes)) {
            batchMessagePrinter.awaitMessages();
        }
    }
}