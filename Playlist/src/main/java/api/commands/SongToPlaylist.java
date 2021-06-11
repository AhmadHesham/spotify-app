package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import config.STATUSCODES;
import db.ArangoConfig;
import models.nosql.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class SongToPlaylist extends Command {

    @Override
    public void execute() throws Exception {
        try{
            String query = "FOR t IN Playlist FILTER t.id == @id RETURN t";
            Map<String, Object> bindVars = Collections.singletonMap("id", map.get("playlist_id"));
            ArangoCursor<BaseDocument> cursor = ArangoConfig.arangoDatabase.query(query, bindVars, null, BaseDocument.class);
            cursor.forEachRemaining(aDocument -> {
                BaseDocument myobj=  ArangoConfig.arangoDatabase.collection("Playlist").getDocument(aDocument.getKey(),BaseDocument.class);
                Song songToAdd= new Song (map.get("song_id"), map.get("song_url"),map.get("song_name"),map.get("cover_photo"));

                ArrayList<Song> a = (ArrayList<Song>) myobj.getAttribute("songs");
                a.add(songToAdd);
                myobj.addAttribute("songs",a);

                ArangoConfig.arangoDatabase.collection("Playlist").updateDocument(aDocument.getKey(),myobj);
                ResponseHandler.handleResponse("Success",map.get("queue"),map.get("correlation_id"));
            });
        }
        catch(ArangoDBException e){
            ResponseHandler.handleError("Failed to add a song to playlist", STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));
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

