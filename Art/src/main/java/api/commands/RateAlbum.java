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

public class RateAlbum extends Command{
    @Override
    public void execute() throws Exception {

        try{

            try {
                CollectionEntity myArangoCollection = ArangoConfig.arangoDatabase.createCollection("AlbumRatings");
                System.out.println("Collection created: " + myArangoCollection.getName());
            } catch(ArangoDBException e) {
                System.out.println("Could not create collection");
            }

            dbConn = PostgresConfig.getDataSource().getConnection();

            func = dbConn.prepareStatement("SELECT * FROM get_album_rating(?);");
            func.setInt(1, Integer.parseInt(map.get("album_id")));
            set = func.executeQuery();

            if (set.next()) {
                JSONObject result = new JSONObject();
                int albumId = Integer.parseInt(map.get("album_id"));
                int userId = Integer.parseInt(map.get("user_id"));
                int userRating = Integer.parseInt(map.get("rating"));
                float albumRating = set.getFloat(1);
                int numberOfRatings = set.getInt(2);

                String getAlbumQuery = "FOR r IN AlbumRatings FILTER r.albumId == " + albumId + " AND r.userId == " + userId + " RETURN r";
                ArangoCursor<BaseDocument> cursor = ArangoConfig.arangoDatabase.query(getAlbumQuery, null, null, BaseDocument.class);
                List<BaseDocument> getAlbumQueryResult = cursor.asListRemaining();
                float newRating = 0;
                if (getAlbumQueryResult.size() == 0) {
                    BaseDocument myObject = new BaseDocument();
                    myObject.addAttribute("albumId", albumId);
                    myObject.addAttribute("userId", userId);
                    myObject.addAttribute("rating", userRating);
                    ArangoConfig.arangoDatabase.collection("AlbumRatings").insertDocument(myObject);

                    newRating = ((albumRating * numberOfRatings) + userRating)/(numberOfRatings + 1);
                    numberOfRatings++;
                }
                else {
                    BaseDocument myObject = getAlbumQueryResult.get(0);
                    int oldUserRating = Integer.parseInt(myObject.getAttribute("rating").toString());
                    albumRating = (albumRating * numberOfRatings) - oldUserRating;
                    newRating = (albumRating + userRating) / numberOfRatings;

                    String updateQuery = "FOR r IN AlbumRatings FILTER r.albumId == " + albumId + "AND r.userId == " + userId + " UPDATE r WITH { rating: " + userRating + " } IN AlbumRatings";
                    ArangoConfig.arangoDatabase.query(updateQuery, null, null, BaseDocument.class);
                }

                result.put("new_rating", newRating);
                proc = dbConn.prepareCall("call rate_album(?,?,?)");
                proc.setInt(1, albumId);
                proc.setDouble(2, newRating);
                proc.setInt(3, numberOfRatings);
                proc.execute();

                ResponseHandler.handleResponse(result.toString(), map.get("queue"), map.get("correlation_id"));
            }
            else{
                ResponseHandler.handleError("This album ID does not exist", STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));
            }


        }
        catch(ArangoDBException e) {
            System.err.println("Failed to create collection: " + e.getMessage());
        }
        catch (Exception e) {
            JSONObject result = new JSONObject();
            result.put("error", "An exception has occurred: Rating Album");
            System.out.println("error: " + e);
            ResponseHandler.handleResponse(result.toString(), map.get("queue"), map.get("correlation_id"));
        }

    }
}
