package im.hdy.entity;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by hdy on 2017/9/8.
 * 存放短信消息
 */
public class Message {
    private String id;
    private LinkedList<Message> messages;

    public Message() {
    }

    public Message(String id, LinkedList<Message> messages) {
        this.id = id;
        this.messages = messages;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LinkedList<Message> getMessages() {
        return messages;
    }

    public void setMessages(LinkedList<Message> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", messages=" + messages +
                '}';
    }
}
