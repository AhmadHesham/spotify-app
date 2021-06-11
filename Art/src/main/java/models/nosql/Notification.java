package models.nosql;

import java.util.HashMap;

public class Notification {
    private String senderID;
    private String receiverID;
    private String type;
    private HashMap<String, String> data;
    private String date;

    public Notification(String senderID, String type, HashMap<String, String> data, String date, String receiverID){
        this.senderID = senderID;
        this.type = type;
        this.data = data;
        this.date = date;
        this.receiverID = receiverID;
    }
}
