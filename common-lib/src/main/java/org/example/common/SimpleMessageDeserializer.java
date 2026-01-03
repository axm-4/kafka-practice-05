package org.example.common;

import org.apache.kafka.common.serialization.Deserializer;

import java.nio.ByteBuffer;

public class SimpleMessageDeserializer implements Deserializer<SimpleMessage> {
    @Override
    public SimpleMessage deserialize(String topic, byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        ByteBuffer buffer = ByteBuffer.wrap(data);
        int textSize = buffer.getInt();
        byte[] textBytes = new byte[textSize];
        buffer.get(textBytes);
        String text = new String(textBytes);
        return new SimpleMessage(text);
    }
}
