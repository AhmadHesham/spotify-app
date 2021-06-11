package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import api.shared.helpers.CloudinaryAPI;
import config.STATUSCODES;
import db.PostgresConfig;


public class DeleteSong extends Command {
    public boolean songExists;
    public String song_url;


    @Override
    public void execute() throws Exception {
        try {
            dbConn = PostgresConfig.getDataSource().getConnection();
            if (songExists) {
                String url = song_url;
                CloudinaryAPI.delete(url);
                proc = dbConn.prepareCall("call delete_song_by_id(?)");
                proc.setPoolable(true);

                proc.setInt(1, Integer.parseInt(map.get("song_id")));

                proc.execute();

                ResponseHandler.handleResponse("Song is deleted", map.get("queue"), map.get("correlation_id"));
            } else {
                ResponseHandler.handleError("Song not found", STATUSCODES.ENTITYNOTFOUND, map.get("queue"), map.get("correlation_id"));
            }
        }
        catch (Exception e){
            ResponseHandler.handleError(e.getMessage(), STATUSCODES.UNKNOWN,map.get("queue"), map.get("correlation_id"));
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

            song_url = set.getString(4);

            if (!songExists) {
                return STATUSCODES.AUTHORIZATION;
            }

            String artist_id = set.getString(7);

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
