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

public class RateArtist extends Command{
    @Override
    public void execute() throws Exception {

        try{

            try {
                CollectionEntity myArangoCollection=ArangoConfig.arangoDatabase.createCollection("ArtistRatings");
                System.out.println("Collection created: " + myArangoCollection.getName());
            } catch(ArangoDBException e) {
                System.out.println("Could not create collection");
            }

            dbConn = PostgresConfig.getDataSource().getConnection();

            func = dbConn.prepareStatement("SELECT * FROM get_artist(?);");
            func.setInt(1, Integer.parseInt(map.get("artist_id")));
            set = func.executeQuery();

            if (set.next()) {
                JSONObject result = new JSONObject();
                int artistId = Integer.parseInt(map.get("artist_id"));
                int userId = Integer.parseInt(map.get("user_id"));
                int userRating = Integer.parseInt(map.get("rating"));
                float artistRating = set.getFloat(1);
                int numberOfRatings = set.getInt(2);

                String getArtistQuery = "FOR r IN ArtistRatings FILTER r.artistId == " + artistId + " AND r.userId == " + userId + " RETURN r";
                ArangoCursor<BaseDocument> cursor = ArangoConfig.arangoDatabase.query(getArtistQuery, null, null, BaseDocument.class);
                List<BaseDocument> getArtistQueryResult = cursor.asListRemaining();
                float newRating = 0;
                if (getArtistQueryResult.size() == 0) {
                    BaseDocument myObject = new BaseDocument();
                    myObject.addAttribute("artistId", artistId);
                    myObject.addAttribute("userId", userId);
                    myObject.addAttribute("rating", userRating);
                    ArangoConfig.arangoDatabase.collection("ArtistRatings").insertDocument(myObject);

                    newRating = ((artistRating * numberOfRatings) + userRating)/(numberOfRatings + 1);
                    numberOfRatings++;
                }
                else {
                    BaseDocument myObject = getArtistQueryResult.get(0);
                    int oldUserRating = Integer.parseInt(myObject.getAttribute("rating").toString());
                    artistRating = (artistRating * numberOfRatings) - oldUserRating;
                    newRating = (artistRating + userRating) / numberOfRatings;

                    String updateQuery = "FOR r IN ArtistRatings FILTER r.artistId == " + artistId + "AND r.userId == " + userId + " UPDATE r WITH { rating: " + userRating + " } IN ArtistRatings";
                    ArangoConfig.arangoDatabase.query(updateQuery, null, null, BaseDocument.class);
                }

                result.put("new_rating", newRating);
                proc = dbConn.prepareCall("call edit_artist(?,?,?)");
                proc.setInt(1, artistId);
                proc.setDouble(2, newRating);
                proc.setInt(3, numberOfRatings);
                proc.execute();

                ResponseHandler.handleResponse(result.toString(), map.get("queue"), map.get("correlation_id"));
            }
            else{
                ResponseHandler.handleError("This artist ID does not exist", STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));
            }


        }
        catch(ArangoDBException e) {
            System.err.println("Failed to create collection: " + e.getMessage());
            //throw exception;
        }
        catch (Exception e) {
            JSONObject result = new JSONObject();
            result.put("error", "An exception has occurred: Rating Artist");
            System.out.println("error: " + e);
            ResponseHandler.handleResponse(result.toString(), map.get("queue"), map.get("correlation_id"));
        }

    }
}
