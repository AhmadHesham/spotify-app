package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import config.STATUSCODES;
import db.PostgresConfig;
import org.json.JSONObject;

public class GetAlbum extends Command {
    @Override
    public void execute() throws Exception {
        try {
             dbConn = PostgresConfig.getDataSource().getConnection();

            func = dbConn.prepareStatement("SELECT * FROM get_album(?);");

            func.setInt(1, Integer.parseInt(map.get("album_id")));
            set = func.executeQuery();


            JSONObject result = new JSONObject();

            if (set.next()) {
                result.put("name", set.getString(1));
                result.put("genre", set.getString(2));
                result.put("cover_photo", set.getString(3));
                result.put("artist_id", set.getInt(4));
            }


            func = dbConn.prepareStatement("SELECT * FROM get_songs_by_album(?);");

            func.setInt(1, Integer.parseInt(map.get("album_id")));
            set = func.executeQuery();


            while (set.next()){
                JSONObject element = new JSONObject();
                element.put("song_name", set.getString(1));
                element.put("song_genre", set.getString(2));
                element.put("rating", set.getString(3));
                element.put("song_url", set.getString(4));
                element.put("total_streams", set.getString(5));
                element.put("release_date", set.getString(6));
                element.put("artist_id", set.getString(7));
                element.put("album_id", set.getString(8));
                result.append("songs", element);
            }
            func.close();
            set.close();
            func = dbConn.prepareStatement("SELECT * FROM get_account(?);");

            func.setInt(1, result.getInt("artist_id"));
            set = func.executeQuery();

            if (set.next()) {
                result.put("artist_name", set.getString(3));
                result.put("profile_photo", set.getString(4));
                result.put("number_of_followers", set.getString(7));
            }

            set.close();
            func.close();


            ResponseHandler.handleResponse(result.toString(), map.get("queue"), map.get("correlation_id"));
        }catch (Exception e) {

            ResponseHandler.handleError(e.getMessage(), STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));
        }
        finally {
            dbConn.close();
        }

    }
}
