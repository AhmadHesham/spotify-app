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

public class RemoveSongFromPlaylist extends Command {


    @Override
    public void execute() throws Exception {

        try{
            String query = "FOR t IN Playlist FILTER t.userId == @userId RETURN t";
            Map<String, Object> bindVars = Collections.singletonMap("userId", map.get("user_id"));
            ArangoCursor<BaseDocument> cursor = ArangoConfig.arangoDatabase.query(query, bindVars, null, BaseDocument.class);
            cursor.forEachRemaining(aDocument -> {
                BaseDocument myobj=  ArangoConfig.arangoDatabase.collection("Playlist").getDocument(aDocument.getKey(),BaseDocument.class);
                if (myobj.getAttribute("id").equals(map.get("playlist_id"))){
                    ArrayList<Song> songsArray = (ArrayList<Song>) myobj.getAttribute("songs");

                    if(!songsArray.isEmpty()){
                        ArrayList<Song> songsArrayAfterRemoving = new ArrayList<Song>();
                        JSONArray songsArrayJson = new JSONArray(songsArray);
                        for (int i = 0; i < songsArray.size() ; i++) {
                            JSONObject currentBlock = songsArrayJson.getJSONObject(i);
                            if(currentBlock.get("id").equals(map.get("song_id"))){
                                songsArray.remove(i);
                            }
                        }
                        myobj.addAttribute("songs",songsArray);
                        ArangoConfig.arangoDatabase.collection("Playlist").updateDocument(aDocument.getKey(),myobj);
                        ResponseHandler.handleResponse("Song was removed sucessfully",map.get("queue"),map.get("correlation_id"));
                    }
                    else{
                        ResponseHandler.handleError("Failed to remove a song from playlist", STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));
                    }}
            });
        }
        catch(ArangoDBException e){
            ResponseHandler.handleError("Failed to remove a song from playlist", STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));
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


