package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import config.STATUSCODES;
import db.ArangoConfig;
import models.nosql.Album;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class RemoveLikedAlbum extends Command {

    @Override
    public void execute() throws Exception {


        try {
            String query = "FOR a IN Albums FILTER a.userId == @userId Return a";
            Map<String, Object> bindVars = Collections.singletonMap("userId", map.get("user_id"));
            ArangoCursor<BaseDocument> cursor = ArangoConfig.arangoDatabase.query(query, bindVars, null, BaseDocument.class);
            cursor.forEachRemaining(aDocument -> {
                BaseDocument myobj = ArangoConfig.arangoDatabase.collection("Albums").getDocument(aDocument.getKey(), BaseDocument.class);
                ArrayList<Album> likes = (ArrayList<Album>) myobj.getAttribute("albums");
                JSONArray likedAlbumString = new JSONArray(likes);
                for (int i = 0; i < likedAlbumString.length(); i++) {
                    JSONObject currentAlbum= likedAlbumString.getJSONObject(i);
                    if(currentAlbum.get("id").equals(map.get("album_id")))
                    {
                        likes.remove(i);
                    }
                }
                if(!likes.isEmpty()){
                    myobj.updateAttribute("albums", likes);
                    ArangoConfig.arangoDatabase.collection("Albums").updateDocument(aDocument.getKey(), myobj);
                    ResponseHandler.handleResponse("Album removed successfully!", map.get("queue"), map.get("correlation_id"));
                }
                else{
                    ArangoConfig.arangoDatabase.collection("Albums").deleteDocument(aDocument.getKey());
                    ResponseHandler.handleResponse("Album removed successfully!", map.get("queue"), map.get("correlation_id"));
                }
            });
        }
        catch (ArangoDBException e) {
            ResponseHandler.handleError("Failed to remove an album", STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));
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

