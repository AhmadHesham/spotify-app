package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import auth.JWT;
import com.arangodb.entity.BaseDocument;
import config.STATUSCODES;
import db.ArangoConfig;
import io.jsonwebtoken.Claims;
import models.nosql.DirectMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LogText extends Command {

    @Override
    public String authorize() throws Exception {
        try{
            String chatID = map.get("chatID");
            Claims claims = JWT.decodeJWT(map.get("logToken"));
            String senderID = (String)claims.get("token_user_id");
//            String senderID = map.get("senderID");
//            String tokenType = (String)claims.get("token_type");
            BaseDocument chatDocument = ArangoConfig.arangoDatabase.collection("chat_test").getDocument(chatID.split("_")[1], BaseDocument.class);
            ArrayList<String> participants = (ArrayList<String>) chatDocument.getAttribute("participants");
            System.out.println("Log Text Chat Map : " + map);
            if(participants.contains(senderID) && map.get("token_type").equals("user")) {
                System.out.println("SUCCESS");
                return STATUSCODES.SUCCESS;
            }
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
        String senderID = map.get("token_user_id");
        String chatID = map.get("chatID");
        String text = map.get("text");
        String senderName = map.get("senderName");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sentDate = formatter.format(new Date());
//        try{
//            if(map.containsKey("participants")){
//                ArrayList<String> participants = new ArrayList<String>();
//                for(int i = 0;i<map.get("participants").split(";").length;i++)
//                    participants.add(map.get("participants").split(";")[i]);
//                ArangoConfig.arangoDatabase.createCollection(chatID);
//                System.out.println("Creating collection...");
//                ArangoConfig.arangoDatabase.collection("chat_tests").insertDocument(new Chat(participants, sentDate));
//            }
//            ArangoCollection chatCollection = ArangoConfig.arangoDatabase.collection(chatID);
//        }
//        catch (ArangoDBException e){
//            System.out.println(e.toString());
//        }
        try{
            DirectMessage dm = new DirectMessage(chatID, senderID, sentDate, senderName, text);
            ArangoConfig.arangoDatabase.collection(chatID).insertDocument(dm);
            ResponseHandler.handleResponse("Message Logged!", map.get("queue"), map.get("correlation_id"));
        }
        catch(Exception e){
            e.printStackTrace();
            ResponseHandler.handleError("Invalid Chat!", STATUSCODES.INVALIDCHAT, map.get("queue"), map.get("correlation_id"));
        }
    }

}
