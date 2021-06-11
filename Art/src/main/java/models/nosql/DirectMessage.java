package models.nosql;

public class DirectMessage {
    private String chatID;
    private String sender_id;
    private String text;
    private String sentDate;
    private String senderName;

    public DirectMessage(String chatID, String sender_id, String senderName, String text){
        this.chatID = chatID;
        this.sender_id = sender_id;
        this.text = text;
        this.senderName = senderName;
    }

    public DirectMessage(String chatID, String sender_id, String sentDate, String senderName, String text){
        this.chatID = chatID;
        this.sender_id = sender_id;
        this.sentDate = sentDate;
        this.text = text;
        this.senderName = senderName;
    }

    public String getChatID() {
        return chatID;
    }

    public String getSender_id() {
        return sender_id;
    }

    public String getSentDate() {
        return sentDate;
    }

    public String getText() {
        return text;
    }
}
