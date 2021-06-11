package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import config.STATUSCODES;
import db.ArangoConfig;
import models.nosql.Song;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class RemoveLikedSong extends Command {

    @Override
    public void execute() throws Exception {

        try {
            String query = "FOR s IN Liked_Songs FILTER s.userId == @userId Return s";
            Map<String, Object> bindVars = Collections.singletonMap("userId", map.get("user_id"));
            ArangoCursor<BaseDocument> cursor = ArangoConfig.arangoDatabase.query(query, bindVars, null, BaseDocument.class);

            cursor.forEachRemaining(aDocument -> {
                BaseDocument myobj = ArangoConfig.arangoDatabase.collection("Liked_Songs").getDocument(aDocument.getKey(), BaseDocument.class);
                ArrayList<Song> likes = (ArrayList<Song>) myobj.getAttribute("likes");
                JSONArray likedSongString = new JSONArray(likes);

                for (int i = 0; i < likedSongString.length(); i++) {
                    JSONObject currentSong= likedSongString.getJSONObject(i);
                    if(currentSong.get("id").equals(map.get("song_id")))
                    {
                        likes.remove(i);
                    }
                }
                if(!likes.isEmpty()) {
                    myobj.addAttribute("likes", likes);
                    ArangoConfig.arangoDatabase.collection("Liked_Songs").updateDocument(aDocument.getKey(), myobj);
                }
                else{
                    ArangoConfig.arangoDatabase.collection("Liked_Songs").deleteDocument(aDocument.getKey());
                }
                ResponseHandler.handleResponse("Song removed successfully!", map.get("queue"), map.get("correlation_id"));
            });
        }
        catch (ArangoDBException e) {
            ResponseHandler.handleError("Failed to remove a liked song", STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));
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

