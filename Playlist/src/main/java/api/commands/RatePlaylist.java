package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.CollectionEntity;
import config.STATUSCODES;
import db.ArangoConfig;
import org.json.JSONObject;

import java.util.List;

public class RatePlaylist extends Command{
    public void execute() {
        try {
            CollectionEntity myArangoCollection = ArangoConfig.arangoDatabase.createCollection("PlaylistRatings");
            System.out.println("Collection created: " + myArangoCollection.getName());

            JSONObject result = new JSONObject();
            int playlist_id = Integer.parseInt(map.get("playlist_id"));
            int user_id = Integer.parseInt(map.get("user_id"));
            int user_rating = Integer.parseInt(map.get("rating"));


            String query = "FOR i IN Playlist FILTER i.id == "+ playlist_id + " return i" ;
            ArangoCursor<BaseDocument>cursor = ArangoConfig.arangoDatabase.query(query, null, null, BaseDocument.class);
            List<BaseDocument> getPlaylistQuery = cursor.asListRemaining();

            if(getPlaylistQuery.size()!=0){
                BaseDocument p1 = getPlaylistQuery.get(0);
                float playlistRating = Float.parseFloat(p1.getAttribute("avgRating").toString());
                int numberOfRatings =  Integer.parseInt(p1.getAttribute("number_of_ratings").toString());
                String queryPlaylist = "FOR i IN PlaylistRatings FILTER i.id == "+ playlist_id+ " AND i.user_id == "+ user_id +" return i";
                ArangoCursor<BaseDocument>cursorPlaylistRating= ArangoConfig.arangoDatabase.query(queryPlaylist, null, null, BaseDocument.class);
                List<BaseDocument> getRateQuery = cursorPlaylistRating.asListRemaining();
                float rate = 0;

                if(getRateQuery.size()==0){
                    BaseDocument newDoc = new BaseDocument();
                    newDoc.addAttribute("playlist_id", playlist_id);
                    newDoc.addAttribute("user_id", user_id);
                    newDoc.addAttribute("rating", user_rating);
                    ArangoConfig.arangoDatabase.collection("PlaylistRatings").insertDocument(newDoc);

                    rate = (playlistRating + user_rating)/(numberOfRatings + 1);
                    p1.updateAttribute("number_of_ratings",  Integer.parseInt(p1.getAttribute("number_of_ratings").toString())+1);
                    p1.updateAttribute("avgRating", rate);

                }
                else{
                    BaseDocument Document = getRateQuery.get(0);
                    int oldUserRating = Integer.parseInt(Document.getAttribute("rating").toString());
                    playlistRating = (playlistRating * numberOfRatings) - oldUserRating;
                    rate = (playlistRating + user_rating) / numberOfRatings;
                    p1.updateAttribute("avgRating", rate);

                    String updateQuery = "FOR r IN PlaylistRatings FILTER r.playlist_id== " + playlist_id + "AND r.user_id == " + user_id + " UPDATE r WITH { rating: " + user_rating + " } IN PlaylistRatings";
                    ArangoConfig.arangoDatabase.query(updateQuery, null, null, BaseDocument.class);
                }
                result.put("new_rating", rate);
            }
            else{
                ResponseHandler.handleError("This Playlist ID does not exist", STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));
            }

        } catch (ArangoDBException e) {
            System.err.println("Failed to create collection: " + "; " + e.getMessage());
        }
        catch (Exception e) {
            JSONObject result = new JSONObject();
            result.put("error", "An exception has occurred: Playlist rating");
            System.out.println("error: " + e);
            ResponseHandler.handleResponse(result.toString(), map.get("queue"), map.get("correlation_id"));
        }

    }

}