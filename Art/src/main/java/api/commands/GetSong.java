package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import config.STATUSCODES;
import db.PostgresConfig;
import org.json.JSONObject;

public class GetSong extends Command {
    @Override
    public void execute() throws Exception {
        try {
             dbConn = PostgresConfig.getDataSource().getConnection();

            func = dbConn.prepareStatement("SELECT * FROM get_song(?);");

            func.setInt(1, Integer.parseInt(map.get("song_id")));
            set = func.executeQuery();


            JSONObject result = new JSONObject();

            if (set.next()) {
                result.put("name", set.getString(1));
                result.put("genre", set.getString(2));
                result.put("rating", set.getString(3));
                result.put("song_url", set.getString(4));
                result.put("total_streams", set.getString(5));
                result.put("release_date", set.getString(6));
                result.put("artist_id", set.getString(7));
                result.put("album_id", set.getString(8));
            }

             dbConn = PostgresConfig.getDataSource().getConnection();

            func = dbConn.prepareStatement("SELECT * FROM get_album(?);");

            func.setInt(1, Integer.parseInt(result.getString("album_id")));
            set = func.executeQuery();


            if(set.next())
                result.put("cover_photo", set.getString(3));



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
