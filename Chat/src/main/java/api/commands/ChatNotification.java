package api.commands;

import api.Command;
import api.shared.FirebaseConfig;
import api.shared.ResponseHandler;
import auth.JWT;
import com.arangodb.entity.BaseDocument;
import config.STATUSCODES;
import db.ArangoConfig;
import io.jsonwebtoken.Claims;
import models.nosql.Notification;
import netty.chat.LinkMap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ChatNotification extends Command {

    @Override
    public String authorize() throws Exception {
//        String senderID = map.get("senderID");
        Claims claims = JWT.decodeJWT(map.get("logToken"));
        String senderID = (String)claims.get("token_user_id");
        String chatID = map.get("chatID");
        try{
            BaseDocument chatDocument = ArangoConfig.arangoDatabase.collection("chat_test").getDocument(chatID.split("_")[1], BaseDocument.class);
            ArrayList<String> participants = (ArrayList<String>) chatDocument.getAttribute("participants");
            if(participants.contains(senderID) && map.get("token_type").equals("user"))
                return STATUSCODES.SUCCESS;
        }
        catch (Exception e){
            e.printStackTrace();
            return STATUSCODES.INVALIDCHAT;
        }
        return STATUSCODES.AUTHORIZATION;
    }

    @Override
    public void execute() throws Exception {
        try{
            if(!ArangoConfig.arangoDatabase.collection("notifications").exists())
                ArangoConfig.arangoDatabase.createCollection("notifications");

//            String senderID = map.get("senderID");
            String senderID = map.get("token_user_id");
            String chatID = map.get("chatID");
            String text = map.get("text");
            String senderName = map.get("senderName");
            HashMap<String, String> data = new HashMap<>();
            data.put("senderName", senderName);
            data.put("text", text);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sentDate = formatter.format(new Date());

            BaseDocument chatDocument = ArangoConfig.arangoDatabase.collection("chat_test").
                    getDocument(chatID.split("_")[1], BaseDocument.class);
            ArrayList<String> participants = (ArrayList<String>) chatDocument.getAttribute("participants");
            ArrayList<String> registrationTokens = new ArrayList<>();
            for(int i = 0;i<participants.size();i++) {
                System.out.println(participants.get(i));
//                if (!participants.get(i).equals(senderID))
                    registrationTokens.add(LinkMap.userToRegistrationToken.get(participants.get(i)));
                Notification notification = new Notification(senderID, "chat", data, sentDate, participants.get(i));
                ArangoConfig.arangoDatabase.collection("notifications").insertDocument(notification);
            }
            FirebaseConfig.sendMessage(data, registrationTokens);
            ResponseHandler.handleResponse("Notifications Sent!", map.get("queue"), map.get("correlation_id"));

        }
        catch(Exception e){
            ResponseHandler.handleError("Invalid Chat", STATUSCODES.INVALIDCHAT, map.get("queue"), map.get("correlation_id"));
        }
    }
}
