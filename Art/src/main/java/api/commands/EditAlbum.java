package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import api.shared.helpers.CloudinaryAPI;
import config.STATUSCODES;
import db.PostgresConfig;

public class EditAlbum extends Command {

    public boolean albumExists;
    @Override
    public void execute() throws Exception {
        try {
            dbConn = PostgresConfig.getDataSource().getConnection();
            proc = dbConn.prepareCall("call edit_album(?,?,?,?)");

            proc.setInt(1, Integer.parseInt(map.get("album_id")));
            proc.setString(2, map.get("album_name"));
            proc.setString(3, map.get("album_genre"));

            if (map.containsKey("photo_path")) {
                String url = CloudinaryAPI.upload(map.get("photo_path"), "album_covers");
                proc.setString(4, url);
            } else {
                proc.setString(4, null);
            }
//        proc.setString(2, map.get(""));

            proc.execute();
            ResponseHandler.handleResponse("Album edited successfully", map.get("queue"), map.get("correlation_id"));
        }
        catch (Exception e){
            e.printStackTrace();
            ResponseHandler.handleError("Cannot edit album", STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));
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
