package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import com.arangodb.ArangoCursor;
import config.STATUSCODES;
import db.ArangoConfig;

import java.util.ArrayList;

public class GetNotifications extends Command {
    @Override
    public String authorize() throws Exception {
        String userType = map.get("token_type");
        if(userType.equals("user"))
            return STATUSCODES.SUCCESS;

        return STATUSCODES.AUTHORIZATION;
    }

    @Override
    public void execute() throws Exception {
        try{
            if(!ArangoConfig.arangoDatabase.collection("notifications").exists())
                ArangoConfig.arangoDatabase.createCollection("notifications");

            String userID = map.get("token_user_id");
            System.out.println("USER ID: " + userID);
            String query = "FOR notif in notifications FILTER notif.receiverID == \"" + userID + "\" RETURN notif";
            ArangoCursor<String> cursor = ArangoConfig.arangoDatabase.query(query, String.class);
            ArrayList<String> notifications = new ArrayList<>();
            for(String document: cursor)
                notifications.add(document);

            ResponseHandler.handleResponse(notifications.toString(), map.get("queue"), map.get("correlation_id"));
        }
        catch (Exception e){
            e.printStackTrace();
            ResponseHandler.handleError("ERROR!", STATUSCODES.NOTFOUND, map.get("queue"), map.get("correlation_id"));
        }
    }
}
