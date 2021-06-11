package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import api.shared.helpers.CloudinaryAPI;
import config.STATUSCODES;
import db.PostgresConfig;

import java.sql.Date;

public class EditSong extends Command {
    public boolean songExists;
    public Date release_date;
    public int album_id;
    @Override
    public void execute() throws Exception {

        try {
            dbConn = PostgresConfig.getDataSource().getConnection();
            proc = dbConn.prepareCall("call edit_song(?,?,?,?,?,?)");
            proc.setInt(1, Integer.parseInt(map.get("song_id")));
            proc.setString(2, map.get("song_name"));
            proc.setString(3, map.get("song_genre"));

            if (map.containsKey("song_url")) {
                String url = CloudinaryAPI.upload(map.get("song_path"), "song");
                proc.setString(4, url);

            } else {
                proc.setString(4, null);
            }
            if(map.containsKey("release_date"))
                proc.setDate(5, Date.valueOf(map.get("release_date")));
            else
                proc.setDate(5, release_date);

            if(map.containsKey("album_id")) {
                proc.setInt(6, Integer.parseInt(map.get("album_id")));
                System.out.println("howa da"+map.get("album_id"));

            }
            else
                proc.setInt(6, album_id);


            proc.execute();

            ResponseHandler.handleResponse("Song edited successfully", map.get("queue"), map.get("correlation_id"));
        }
        catch (Exception e){
            e.printStackTrace();
            ResponseHandler.handleError("Cannot edit song", STATUSCODES.UNKNOWN, map.get("queue"), map.get("correlation_id"));
        }
        finally {
            dbConn.close();
        }

    }

    @Override
    public String authorize() throws Exception {
        try {
            dbConn = PostgresConfig.getDataSource().getConnection();

            func = dbConn.prepareStatement("SELECT * FROM get_song(?);");

            func.setInt(1, Integer.parseInt(map.get("song_id")));
            set = func.executeQuery();

            songExists = set.next();


            if (!songExists) {
                return STATUSCODES.AUTHORIZATION;
            }
            release_date = set.getDate(6);
            album_id = set.getInt(8);

            String artist_id = set.getString(7);

            if (map.get("token_user_id").equals(artist_id)) {
                return STATUSCODES.SUCCESS;
            }

            if (map.containsKey("album_id")) {
                dbConn = PostgresConfig.getDataSource().getConnection();

                func = dbConn.prepareStatement("SELECT * FROM get_album(?);");

                func.setInt(1, Integer.parseInt(map.get("album_id")));
                set = func.executeQuery();

                if (map.get("token_user_id").equals(set.getString(4))) {
                    return STATUSCODES.SUCCESS;
                }
                return STATUSCODES.AUTHORIZATION;
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
