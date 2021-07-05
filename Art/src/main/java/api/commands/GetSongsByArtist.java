package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import config.STATUSCODES;
import db.PostgresConfig;
import org.json.JSONObject;

import java.util.HashMap;

public class GetSongsByArtist extends Command {
    @Override
    public void execute() throws Exception {
        try {
            dbConn = PostgresConfig.getDataSource().getConnection();

            func = dbConn.prepareStatement("SELECT * FROM get_albums_by_artist(?);");
            func.setInt(1, Integer.parseInt(map.get("artist_id")));
            set = func.executeQuery();


            JSONObject result = new JSONObject();
            HashMap<Integer, String> album_id_to_cover_photo = new HashMap<>();
            HashMap<Integer, String> album_id_to_name = new HashMap<>();

            while (set.next()){
                album_id_to_cover_photo.put(set.getInt(1), set.getString(4));
                album_id_to_name.put(set.getInt(1), set.getString(2));
            }


            func = dbConn.prepareStatement("SELECT * FROM get_songs_by_artist(?);");
            func.setInt(1, Integer.parseInt(map.get("artist_id")));
            set = func.executeQuery();


            while (set.next()) {
                JSONObject element = new JSONObject();
                element.put("song_name", set.getString(1));
                element.put("song_genre", set.getString(2));
                element.put("rating", set.getString(3));
                element.put("song_url", set.getString(4));
                element.put("total_streams", set.getString(5));
                element.put("release_date", set.getString(6));
                element.put("cover_photo", album_id_to_cover_photo.get(set.getString(8)));
                element.put("album_name", album_id_to_name.get(set.getString(8)));
                result.append("album"+set.getString(8), element);
            }





            dbConn = PostgresConfig.getDataSource().getConnection();

            func = dbConn.prepareStatement("SELECT * FROM get_account(?);");

            func.setInt(1, Integer.parseInt(map.get("artist_id")));
            set = func.executeQuery();

            if (set.next()) {
                result.put("artist_name", set.getString(3));
                result.put("profile_photo", set.getString(4));
                result.put("number_of_followers", set.getString(7));
            }



            ResponseHandler.handleResponse(result.toString(), map.get("queue"), map.get("correlation_id"));
        }
        catch (Exception e){
            ResponseHandler.handleError(e.getMessage(), STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));
        }
    }


}