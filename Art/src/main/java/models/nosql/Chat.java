package models.nosql;

import java.util.ArrayList;

public class Chat {
    private ArrayList<String> participants;
    private String creationDate;

    public Chat(ArrayList<String> participants, String creationDate){
        this.participants = participants;
        this.creationDate = creationDate;
    }

    public Chat(ArrayList<String> participants){
        this.participants = participants;
    }

    public void addParticipant(String id){
        participants.add(id);
    }

    public ArrayList<String> getParticipants() {
        return participants;
    }

    public String getCreationDate() {
        return creationDate;
    }
}
