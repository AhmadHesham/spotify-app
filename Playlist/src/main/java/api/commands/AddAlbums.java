package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.CollectionEntity;
import config.STATUSCODES;
import db.ArangoConfig;
import models.nosql.Album;
import models.nosql.LikedAlbums;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class AddAlbums extends Command {
    @Override
    public void execute() throws Exception {
//        try {
//            CollectionEntity myArangoCollection=ArangoConfig.arangoDatabase.createCollection("Albums");
//            System.out.println("Collection created: " + myArangoCollection.getName());
//        } catch(ArangoDBException e) {
//            JSONObject responseObject = new JSONObject();
//            ResponseHandler.handleError(responseObject.toString(), STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));
//        }
        try {
            if(!ArangoConfig.arangoDatabase.collection("Albums").exists()){
                CollectionEntity myArangoCollection=ArangoConfig.arangoDatabase.createCollection("Albums");
                System.out.println("Collection created: " + myArangoCollection.getName());
            }
            String query = "FOR a IN Albums FILTER a.userId == @userId Return a";
            Map<String, Object> bindVars = Collections.singletonMap("userId", map.get("user_id"));
            ArangoCursor<BaseDocument> cursor = ArangoConfig.arangoDatabase.query(query, bindVars, null, BaseDocument.class);
            if(cursor.hasNext()){
                cursor.forEachRemaining(aDocument -> {
                    BaseDocument myobj = ArangoConfig.arangoDatabase.collection("Albums").getDocument(aDocument.getKey(), BaseDocument.class);
                    Album albumToAdd = new Album(map.get("album_id"), map.get("album_url"), map.get("album_name"), map.get("cover_photo"));

                    ArrayList<Album> likes = (ArrayList<Album>) myobj.getAttribute("albums");
                    JSONArray likedAlbumString = new JSONArray(likes);
                    ArrayList<Integer> albumsIds = new ArrayList<Integer>();

                    for (int i = 0; i < likedAlbumString.length(); i++) {
                        JSONObject eachAlbum = likedAlbumString.getJSONObject(i);
                        albumsIds.add(Integer.parseInt((String) eachAlbum.get("id")));
                    }

                    boolean albumExists = albumsIds.contains(Integer.parseInt(map.get("album_id")));
                    if(!albumExists){
                        likes.add(albumToAdd);
                        myobj.addAttribute("albums", likes);
                        ArangoConfig.arangoDatabase.collection("Albums").updateDocument(aDocument.getKey(), myobj);
                        ResponseHandler.handleResponse("Request successful, album is added!", map.get("queue"), map.get("correlation_id"));
                        return;
                    }
                    else{
                        ResponseHandler.handleResponse("Request successful, album already exists", map.get("queue"), map.get("correlation_id"));
                        return;
                    }

                }); }
            else{
                ArangoConfig.arangoDatabase.collection("Albums").insertDocument(LikedAlbums.createLikedAlbums(map.get("user_id"),
                        new Album(map.get("album_id"), map.get("album_url"), map.get("album_name"), map.get("cover_photo"))));
                ResponseHandler.handleResponse("Request successful, album is added!", map.get("queue"), map.get("correlation_id"));
                return;
            }
        }
        catch (ArangoDBException e) {
            ResponseHandler.handleError("Failed to add an album", STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));
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

