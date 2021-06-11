package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.CollectionEntity;
import config.STATUSCODES;
import db.ArangoConfig;
import models.nosql.LikedSongs;
import models.nosql.Song;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class LikeSongs extends Command {
    //public static boolean songsinDB= false;
    @Override
    public void execute() throws Exception {
//        try {
//            CollectionEntity myArangoCollection=ArangoConfig.arangoDatabase.createCollection("Liked_Songs");
//            System.out.println("Collection created: " + myArangoCollection.getName());
//        } catch(ArangoDBException e) {
//            JSONObject responseObject = new JSONObject();
//            ResponseHandler.handleError(responseObject.toString(), STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));
//        }
        try {
            if(!ArangoConfig.arangoDatabase.collection("Liked_Songs").exists()){
                CollectionEntity myArangoCollection=ArangoConfig.arangoDatabase.createCollection("Liked_Songs");
                System.out.println("Collection created: " + myArangoCollection.getName());
            }
            String query = "FOR a IN Liked_Songs FILTER a.userId == @userId Return a";
            Map<String, Object> bindVars = Collections.singletonMap("userId", map.get("user_id"));
            ArangoCursor<BaseDocument> cursor = ArangoConfig.arangoDatabase.query(query, bindVars, null, BaseDocument.class);
            if(cursor.hasNext()){
                cursor.forEachRemaining(aDocument -> {
                    BaseDocument myobj=  ArangoConfig.arangoDatabase.collection("Liked_Songs").getDocument(aDocument.getKey(),BaseDocument.class);
                    Song songToAdd= new Song (map.get("song_id"), map.get("song_url"),map.get("song_name"),map.get("cover_photo"));

                    ArrayList<Song> likes = (ArrayList<Song>) myobj.getAttribute("likes");
                    JSONArray likedSongString = new JSONArray(likes);
                    ArrayList<Integer> songIds= new ArrayList<Integer>();

                    for( int i = 0 ; i < likedSongString.length() ; i++ ){
                        JSONObject eachSong = likedSongString.getJSONObject(i);
                        songIds.add(Integer.parseInt((String) eachSong.get("id")));
                    }

                    boolean songExists= songIds.contains(Integer.parseInt(map.get("song_id")));
//                    songsinDB=songExists;

                    if(!songExists){
                        likes.add(songToAdd);
                        myobj.addAttribute("likes",likes);
                        ArangoConfig.arangoDatabase.collection("Liked_Songs").updateDocument(aDocument.getKey(),myobj);
                        ResponseHandler.handleResponse("Request successful, song is added", map.get("queue"), map.get("correlation_id"));
                    }
                    else{
                        ResponseHandler.handleResponse("Request successful, song already exists", map.get("queue"), map.get("correlation_id"));
                    }
                });
            }
            else{
                ArangoConfig.arangoDatabase.collection("Liked_Songs").insertDocument(LikedSongs.createLikedSongs(map.get("user_id"),
                        new Song(map.get("song_id"), map.get("song_url"), map.get("song_name"), map.get("cover_photo"))));
                ResponseHandler.handleResponse("Request successful, song is added", map.get("queue"), map.get("correlation_id"));
            }
        }
        catch (ArangoDBException e) {
            ResponseHandler.handleError("Failed to like a song", STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));
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

