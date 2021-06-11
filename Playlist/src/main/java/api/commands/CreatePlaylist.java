package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.CollectionEntity;
import config.STATUSCODES;
import db.ArangoConfig;
import models.nosql.Playlist;

public class CreatePlaylist extends Command {
    @Override
    public void execute() throws Exception {
//        try {
//            CollectionEntity myArangoCollection=ArangoConfig.arangoDatabase.createCollection("Playlist");
//            System.out.println("Collection created: " + myArangoCollection.getName());
//        } catch(ArangoDBException e) {
//            JSONObject responseObject = new JSONObject();
//            ResponseHandler.handleError(responseObject.toString(), STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));
//        }
        try{
            if(!ArangoConfig.arangoDatabase.collection("Playlist").exists()){
                CollectionEntity myArangoCollection=ArangoConfig.arangoDatabase.createCollection("Playlist");
                System.out.println("Collection created: " + myArangoCollection.getName());
            }
            String key = ArangoConfig.arangoDatabase.collection("Playlist")
                    .insertDocument(Playlist.createPlaylist(map.get("playlist_name"),map.get("user_id"), map.get("visibility")))
                    .getKey();
            BaseDocument playlistEntry= ArangoConfig.arangoDatabase.collection("Playlist").getDocument(key,BaseDocument.class);
            ResponseHandler.handleResponse("Playlist created successfully: " + playlistEntry.getAttribute("id"),map.get("queue"),map.get("correlation_id"));
        }
        catch (ArangoDBException e) {
            e.printStackTrace();
            ResponseHandler.handleError("Failed to create a playlist", STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));
        }
    }

    @Override
    public String authorize() {

        if (map.get("token_user_id").equals(map.get("user_id"))) {
            return STATUSCODES.SUCCESS;
        }

        return STATUSCODES.AUTHORIZATION;
    }
}

