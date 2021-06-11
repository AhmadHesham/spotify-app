package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import api.shared.helpers.CloudinaryAPI;
import config.STATUSCODES;
import db.PostgresConfig;

public class DeleteAlbum extends Command {

    public boolean albumExists;
    @Override
    public void execute() throws Exception {
        try {
            dbConn = PostgresConfig.getDataSource().getConnection();
            if (albumExists) {
                String url = set.getString(3);
                CloudinaryAPI.delete(url);
                proc = dbConn.prepareCall("call delete_album(?)");
                proc.setPoolable(true);

                proc.setInt(1, Integer.parseInt(map.get("album_id")));

                proc.execute();

                ResponseHandler.handleResponse("Album is deleted", map.get("queue"), map.get("correlation_id"));
            } else {
                ResponseHandler.handleError("Album not found", STATUSCODES.ENTITYNOTFOUND, map.get("queue"), map.get("correlation_id"));
            }
        }
        catch (Exception e){
            ResponseHandler.handleError(e.getMessage(), STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));
        }
        finally {
            dbConn.close();
        }
    }

    @Override
    public String authorize() throws Exception {
        try {
            dbConn = PostgresConfig.getDataSource().getConnection();

            func = dbConn.prepareStatement("SELECT * FROM get_album(?);");

            func.setInt(1, Integer.parseInt(map.get("album_id")));
            set = func.executeQuery();
            albumExists = set.next();
            if (!albumExists) {
                return STATUSCODES.AUTHORIZATION;
            }

            String artist_id = set.getString(4);

            if (map.get("token_user_id").equals(artist_id)) {
                return STATUSCODES.SUCCESS;
            }
            return STATUSCODES.AUTHORIZATION;
        }
        catch (Exception e){
            return STATUSCODES.AUTHORIZATION;
        }
        finally {
            dbConn.close();
        }
    }
}
