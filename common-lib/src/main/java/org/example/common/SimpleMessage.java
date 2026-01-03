package org.example.common;

import java.io.Serializable;

/**
 * Модель сообщения.
 */
public final class SimpleMessage implements Serializable {

    private final String text;

    public SimpleMessage(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "SimpleMessage{" +
                "text='" + text + '\'' +
                '}';
    }
}
