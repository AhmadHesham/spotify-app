package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import api.shared.helpers.CloudinaryAPI;
import config.STATUSCODES;
import db.PostgresConfig;

public class CreateAlbum extends Command {
    @Override
    public void execute() throws Exception {
        try {
            dbConn = PostgresConfig.getDataSource().getConnection();
//        dbConn.setAutoCommit(false);
            proc = dbConn.prepareCall("call create_album(?,?,?,?)");
            proc.setPoolable(true);

            if(map.get("photo_path") == null){
                proc.setString(3, "https://res.cloudinary.com/spotify-scalable/image/upload/v1620395043/blankart_lqdvyn.jpg");
            }
            else {
                String url = CloudinaryAPI.upload(map.get("photo_path"), "album_covers");
                if(url.equals("Error")){
                    ResponseHandler.handleError("Photo cant be uploaded", STATUSCODES.UPLOADERROR, map.get("queue"), map.get("correlation_id"));
                    return;
                }
                proc.setString(3, url);
            }
            proc.setString(1, map.get("album_name"));
            proc.setString(2, map.get("album_genre"));
            proc.setInt(4, Integer.parseInt(map.get("artist_id")));

            proc.execute();

            ResponseHandler.handleResponse("Album added successfully", map.get("queue"), map.get("correlation_id"));
        }
        catch (Exception e){
            e.printStackTrace();
            ResponseHandler.handleError(e.getMessage(), STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));
        }
        finally {
            dbConn.close();
        }
    }

    @Override
    public String authorize() {
        System.out.println("map yasta" +map.toString());
        if(map.get("token_type").equals("artist") && map.get("token_user_id").equals(map.get("artist_id"))){
            return STATUSCODES.SUCCESS;
        }
        return STATUSCODES.AUTHORIZATION;
    }

}
