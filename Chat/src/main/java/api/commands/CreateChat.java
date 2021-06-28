package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import com.arangodb.entity.DocumentEntity;
import config.STATUSCODES;
import db.ArangoConfig;
import db.PostgresConfig;
import models.nosql.Chat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CreateChat extends Command {

    @Override
    public String authorize() {
        if (map.get("token_type").equals("user"))
            return STATUSCODES.SUCCESS;
        else
            return STATUSCODES.AUTHORIZATION;
    }

    @Override
    public void execute() throws Exception {
        try {
            if (!ArangoConfig.arangoDatabase.collection("chat_test").exists())
                ArangoConfig.arangoDatabase.createCollection("chat_test");
            ArrayList<String> participants = new ArrayList<String>();
            for (int i = 0; i < map.get("participants").split(";").length; i++)
                participants.add(map.get("participants").split(";")[i]);
            dbConn = PostgresConfig.getDataSource().getConnection();
            func = dbConn.prepareStatement("SELECT * FROM get_user(?);");
            for (int i = 0; i < participants.size(); i++) {
                func.setInt(1, Integer.parseInt(participants.get(i)));
                set = func.executeQuery();
                if (!set.next()) {
                    ResponseHandler.handleError("User does not exist", STATUSCODES.INVALIDUSER, map.get("queue"),
                            map.get("correlation_id"));
                    return;
                }
            }

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sentDate = formatter.format(new Date());
            Chat currentChat = new Chat(participants, sentDate);

            DocumentEntity chatInfo = ArangoConfig.arangoDatabase.collection("chat_test").insertDocument(currentChat);
            ArangoConfig.arangoDatabase.createCollection("chat_" + chatInfo.getKey());
            dbConn.close();
            System.out.println("Creating Chat");
            ResponseHandler.handleResponse("chat_" + chatInfo.getKey(), map.get("queue"), map.get("correlation_id"));
        } catch (Exception e) {
            ResponseHandler.handleError("Invalid Chat", STATUSCODES.INVALIDCHAT, map.get("queue"),
                    map.get("correlation_id"));
            System.out.println(e.toString());
        }

    }
}
