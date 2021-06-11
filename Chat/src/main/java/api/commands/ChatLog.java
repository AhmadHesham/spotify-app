package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import com.arangodb.ArangoCursor;
import com.arangodb.entity.BaseDocument;
import config.STATUSCODES;
import db.ArangoConfig;

import java.util.ArrayList;

public class ChatLog extends Command {

    @Override
    public String authorize() {
        try{
            String chatID = map.get("chatID");
            String senderID = map.get("token_user_id"); //will be changed to be retrieved from token later on
            BaseDocument chatDocument = ArangoConfig.arangoDatabase.collection("chat_test").getDocument(chatID.split("_")[1], BaseDocument.class);
            ArrayList<String> participants = (ArrayList<String>) chatDocument.getAttribute("participants");
            if(participants.contains(senderID) && map.get("token_type").equals("user"))
                return STATUSCODES.SUCCESS;
        }
        catch (Exception e){
//            ResponseHandler.handleError("Invalid Chat!", STATUSCODES.INVALIDCHAT, map.get("queue"), map.get("correlation_id"));
            e.printStackTrace();
            return STATUSCODES.INVALIDCHAT;
        }
//        ResponseHandler.handleError("not authorized", STATUSCODES.AUTHORIZATION, map.get("queue"), map.get("correlation_id"));
        return STATUSCODES.AUTHORIZATION;
    }

    @Override
    public void execute() throws Exception {
        try{
            System.out.println("Map In Chat: " + map);
            System.out.println("GETTING CHAT LOGS");
            String chatID = map.get("chatID");
            String query = "FOR chatLog in " + chatID + " RETURN chatLog";
            ArangoCursor<String> cursor = ArangoConfig.arangoDatabase.query(query, String.class);
            ArrayList<String> logs = new ArrayList<>();
            for (String document: cursor) {
                logs.add(document);
            }
            ResponseHandler.handleResponse(logs.toString(), map.get("queue"), map.get("correlation_id"));
        }
        catch(Exception e){
            e.printStackTrace();
            ResponseHandler.handleError("Invalid Chat Query!", STATUSCODES.INVALIDCHATQUERY, map.get("queue"), map.get("correlation_id"));
        }
    }
}
