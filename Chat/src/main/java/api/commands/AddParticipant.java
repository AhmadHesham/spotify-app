package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import com.arangodb.entity.BaseDocument;
import config.STATUSCODES;
import db.ArangoConfig;
import db.PostgresConfig;

import java.util.ArrayList;

public class AddParticipant extends Command {

    @Override
    public String authorize() {
        String chatID = map.get("chatID");
        String senderID = map.get("token_user_id");
        try{
            BaseDocument chatDocument = ArangoConfig.arangoDatabase.collection("chat_test").getDocument(chatID.split("_")[1], BaseDocument.class);
            ArrayList<String> participants = (ArrayList<String>) chatDocument.getAttribute("participants");
            if(participants.contains(senderID) && map.get("token_type").equals("user"))
                return STATUSCODES.SUCCESS;
        }
        catch(Exception e){
//            ResponseHandler.handleError("Invalid Chat!", STATUSCODES.INVALIDCHAT, map.get("queue"), map.get("correlation_id"));
            return STATUSCODES.INVALIDCHAT;
        }
//        ResponseHandler.handleError("not authorized", STATUSCODES.AUTHORIZATION, map.get("queue"), map.get("correlation_id"));
        return STATUSCODES.AUTHORIZATION;
    }

    @Override
    public void execute() throws Exception {
        String chatID = map.get("chatID");
        String userID = map.get("userID");
        try{
            BaseDocument chatDocument = ArangoConfig.arangoDatabase.collection("chat_test").getDocument(chatID.split("_")[1], BaseDocument.class);
            ArrayList<String> participants = (ArrayList<String>) chatDocument.getAttribute("participants");
            if(!participants.contains(userID)){
                 dbConn = PostgresConfig.getDataSource().getConnection();
                func = dbConn.prepareStatement("SELECT * FROM get_user(?);");
                func.setInt(1, Integer.parseInt(userID));
                set = func.executeQuery();
                System.out.println(set.toString());
                if(!set.next()){
                    ResponseHandler.handleError("User does not exist", STATUSCODES.INVALIDUSER, map.get("queue"), map.get("correlation_id"));
                    return;
                }

                participants.add(userID);
                chatDocument.addAttribute("participants", participants);
                System.out.println(chatDocument.getKey());
                ArangoConfig.arangoDatabase.collection("chat_test").updateDocument(chatDocument.getKey(), chatDocument);
                dbConn.close();
                ResponseHandler.handleResponse("User added successfully", map.get("queue"), map.get("correlation_id"));
            }
            else{
                ResponseHandler.handleError("User already in the chat!", STATUSCODES.USERALREADYINCHAT, map.get("queue"), map.get("correlation_id"));
            }
        }
        catch(Exception e){
            ResponseHandler.handleError("Invalid Chat!", STATUSCODES.INVALIDCHAT, map.get("queue"), map.get("correlation_id"));
        }
    }
}
