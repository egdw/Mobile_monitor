package im.hdy.entity;

/**
 * Created by hdy on 2017/9/8.
 */
public class MessageDetail {
    private String messageName;
    private String messageNum;
    private String messageTime;
    private String messgaeText;

    public MessageDetail(String messageName, String messageNum, String messageTime, String messgaeText) {
        this.messageName = messageName;
        this.messageNum = messageNum;
        this.messageTime = messageTime;
        this.messgaeText = messgaeText;
    }

    public MessageDetail() {
    }

    public String getMessageName() {
        return messageName;
    }

    public void setMessageName(String messageName) {
        this.messageName = messageName;
    }

    public String getMessageNum() {
        return messageNum;
    }

    public void setMessageNum(String messageNum) {
        this.messageNum = messageNum;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessgaeText() {
        return messgaeText;
    }

    public void setMessgaeText(String messgaeText) {
        this.messgaeText = messgaeText;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageName='" + messageName + '\'' +
                ", messageNum='" + messageNum + '\'' +
                ", messageTime='" + messageTime + '\'' +
                ", messgaeText='" + messgaeText + '\'' +
                '}';
    }
}
