package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import api.shared.helpers.CloudinaryAPI;
import config.STATUSCODES;
import db.PostgresConfig;

import java.sql.Date;
import java.sql.SQLException;

public class CreateSong extends Command {


    @Override
    public void execute() throws SQLException {
        try {
             dbConn = PostgresConfig.getDataSource().getConnection();
//        dbConn.setAutoCommit(false);
            proc = dbConn.prepareCall("call create_song(?,?,?,?,?,?)");
            proc.setPoolable(true);


            if(map.get("song_path") == null){
                proc.setString(3, "null");
            }
            else {
                String url = CloudinaryAPI.upload(map.get("song_path"), "song", "auto");
                if (url.equals("Error")) {
                    ResponseHandler.handleError("Song cannot be uploaded", STATUSCODES.UPLOADERROR,
                            map.get("queue"), map.get("correlation_id"));
                    return;
                }
                proc.setString(3, url);
            }
            proc.setString(1, map.get("song_name"));
            proc.setString(2, map.get("song_genre"));
            proc.setDate(4, Date.valueOf(map.get("release_date")));
            proc.setInt(5, Integer.parseInt(map.get("artist_id")));
            proc.setInt(6, Integer.parseInt(map.get("album_id")));

            proc.execute();
            System.out.println(map.get("queue"));
            System.out.println(map.get("correlation_id"));
            ResponseHandler.handleResponse("eshta awi el kalam", map.get("queue"), map.get("correlation_id"));
        } catch (Exception e) {
            System.out.println("eh ba2a hwa exception walla eh");
            e.printStackTrace();
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

            set.next();

            String artist_id_album = set.getString(4);


            if (map.get("token_type").equals("artist") && map.get("token_user_id").equals(map.get("artist_id")) &&
                    artist_id_album.equals(map.get("token_user_id"))) {
                return STATUSCODES.SUCCESS;
            }
            return STATUSCODES.AUTHORIZATION;
        }catch (Exception e){
            e.printStackTrace();
            return STATUSCODES.AUTHORIZATION;
        }
        finally {
            dbConn.close();
        }
    }
}
