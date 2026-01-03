package org.example.common;

import org.apache.kafka.common.serialization.Serializer;

import java.nio.ByteBuffer;

public class SimpleMessageSerializer implements Serializer<SimpleMessage> {
    @Override
    public byte[] serialize(String topic, SimpleMessage simpleMessage) {
        if (simpleMessage == null) {
            return null;
        }
        byte[] textBytes = simpleMessage.getText().getBytes();
        int textSize = textBytes.length;
        ByteBuffer buffer = ByteBuffer.allocate(4 + textSize);
        buffer.putInt(textSize);
        buffer.put(textBytes);
        byte[] messageBytes = buffer.array();
        System.out.printf("Сериализовано сообщение размером %d байт\n", messageBytes.length);
        return messageBytes;
    }
}
