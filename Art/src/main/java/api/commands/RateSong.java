package api.commands;
import api.Command;
import api.shared.ResponseHandler;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.CollectionEntity;
import config.STATUSCODES;
import db.ArangoConfig;
import db.PostgresConfig;
import org.json.JSONObject;

import java.util.List;

public class RateSong extends Command {
    @Override
    public void execute() throws Exception {
        try{

            try {
                CollectionEntity myArangoCollection = ArangoConfig.arangoDatabase.createCollection("SongRatings");
                System.out.println("Collection created: " + myArangoCollection.getName());
            } catch(ArangoDBException e) {
                System.out.println("Could not create collection");
            }

            dbConn = PostgresConfig.getDataSource().getConnection();

            func = dbConn.prepareStatement("SELECT * FROM get_song_rating(?);");
            func.setInt(1, Integer.parseInt(map.get("song_id")));
            set = func.executeQuery();

            if (set.next()) {
                JSONObject result = new JSONObject();
                int songId = Integer.parseInt(map.get("song_id"));
                int userId = Integer.parseInt(map.get("user_id"));
                int userRating = Integer.parseInt(map.get("rating"));
                float songRating = set.getFloat(1);
                int numberOfRatings = set.getInt(2);

                String getSongQuery = "FOR r IN SongRatings FILTER r.songId == " + songId + " AND r.userId == " + userId + " RETURN r";
                ArangoCursor<BaseDocument> cursor = ArangoConfig.arangoDatabase.query(getSongQuery, null, null, BaseDocument.class);
                List<BaseDocument> getSongQueryResult = cursor.asListRemaining();
                float newRating = 0;
                if (getSongQueryResult.size() == 0) {
                    BaseDocument myObject = new BaseDocument();
                    myObject.addAttribute("songId", songId);
                    myObject.addAttribute("userId", userId);
                    myObject.addAttribute("rating", userRating);
                    ArangoConfig.arangoDatabase.collection("SongRatings").insertDocument(myObject);

                    newRating = ((songRating * numberOfRatings) + userRating)/(numberOfRatings + 1);
                    numberOfRatings++;
                }
                else {
                    BaseDocument myObject = getSongQueryResult.get(0);
                    int oldUserRating = Integer.parseInt(myObject.getAttribute("rating").toString());
                    songRating = (songRating * numberOfRatings) - oldUserRating;
                    newRating = (songRating + userRating) / numberOfRatings;

                    String updateQuery = "FOR r IN SongRatings FILTER r.songId == " + songId + "AND r.userId == " + userId + " UPDATE r WITH { rating: " + userRating + " } IN SongRatings";
                    ArangoConfig.arangoDatabase.query(updateQuery, null, null, BaseDocument.class);
                }

                result.put("new_rating", newRating);
                proc = dbConn.prepareCall("call rate_song(?,?,?)");
                proc.setInt(1, songId);
                proc.setDouble(2, newRating);
                proc.setInt(3, numberOfRatings);
                proc.execute();

                ResponseHandler.handleResponse(result.toString(), map.get("queue"), map.get("correlation_id"));
            }
            else{
                ResponseHandler.handleError("This song ID does not exist", STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));
            }


        }
        catch(ArangoDBException e) {
            System.err.println("Failed to create collection: " + e.getMessage());
        }
        catch (Exception e) {
            JSONObject result = new JSONObject();
            result.put("error", "An exception has occurred: Rating Song");
            System.out.println("error: " + e);
            ResponseHandler.handleResponse(result.toString(), map.get("queue"), map.get("correlation_id"));
        }
    }
}


