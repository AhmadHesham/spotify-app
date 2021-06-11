package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import config.STATUSCODES;
import db.PostgresConfig;
import org.json.JSONObject;

public class GetAlbums extends Command {
    @Override
    public void execute() throws Exception {
        try {
             dbConn = PostgresConfig.getDataSource().getConnection();

            func = dbConn.prepareStatement("SELECT * FROM get_albums();");
            set = func.executeQuery();


            JSONObject result = new JSONObject();
            while (set.next()) {
                JSONObject element = new JSONObject();
                element.put("id", set.getString(1));
                element.put("name", set.getString(2));
                element.put("genre", set.getString(3));
                element.put("cover_photo", set.getString(4));
                element.put("artist_id", set.getInt(5));
                result.append("data", element);
            }


            set.close();
            func.close();


            ResponseHandler.handleResponse(result.toString(), map.get("queue"), map.get("correlation_id"));
        }
        catch (Exception e){
            ResponseHandler.handleError(e.getMessage(), STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));
        }
        finally {
            dbConn.close();
        }
    }
}
