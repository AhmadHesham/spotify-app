package api.commands;


import api.Command;
import api.shared.ResponseHandler;
import db.PostgresConfig;
import org.json.JSONObject;

import java.util.Locale;

public class GetSongsByGenre extends Command {
    @Override
    public void execute() throws Exception {
        dbConn = PostgresConfig.getDataSource().getConnection();

        func = dbConn.prepareStatement("SELECT * FROM get_songs_by_genre(?);");
        func.setString(1, map.get("genre").toLowerCase(Locale.ROOT));
        set = func.executeQuery();

        JSONObject result = new JSONObject();
        while (set.next()) {
            JSONObject element = new JSONObject();
            element.put("name", set.getString(1));
            element.put("genre", set.getString(2));
            element.put("rating", set.getString(3));
            element.put("song_url", set.getString(4));
            element.put("total_streams", set.getString(5));
            element.put("release_date", set.getString(6));
            element.put("artist_id", set.getString(7));
            element.put("album_id", set.getString(8));
            result.append("data", element);
        }

        ResponseHandler.handleResponse(result.toString(), map.get("queue"), map.get("correlation_id"));
    }
}