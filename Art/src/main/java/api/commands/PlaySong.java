package api.commands;

import api.Command;
import api.shared.ResponseHandler;
import cache.Redis;
import com.arangodb.ArangoCursor;
import com.arangodb.entity.BaseDocument;
import config.STATUSCODES;
import db.ArangoConfig;
import db.PostgresConfig;
import models.nosql.Song;
import models.nosql.SongHistory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.redisson.Redisson;
import org.redisson.api.RMapCache;
import rabbitmq.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PlaySong extends Command {

    String genre;
    String user_id;
    String song_id;
    String song_url;
    String song_name;

    @Override
    public void execute() throws Exception {

        try {
            dbConn = PostgresConfig.getDataSource().getConnection();


            func = dbConn.prepareStatement("SELECT * FROM get_user(?);");

            func.setInt(1, Integer.parseInt(map.get("token_user_id")));
            set = func.executeQuery();

            boolean is_premium = false;
            int bit_rate = 320;
            if (set.next()) {
                is_premium = set.getBoolean(1);
                bit_rate = set.getInt(2);
            }
            song_id = map.get("song_id");
            boolean cached = Redis.hasKey(song_id);

            if(cached){
                String[] urlCached = Redis.get(song_id).split("/");
                String public_id = "";
                for (int i = 7; i < urlCached.length; i++) {
                    public_id += urlCached[i] + "/";
                }
                public_id = public_id.substring(0, public_id.length() - 5);

                String url = "http://138.68.99.91/" + public_id + "/" + bit_rate;

                String totalStreams = set.getString("total_streams");
                int totalStreamsInt = Integer.parseInt(totalStreams) + 1;
                totalStreams = String.valueOf(totalStreamsInt);
//                set.updateString("total_streams", totalStreams);
//                set.updateRow();


                JSONObject result = new JSONObject();

                result.put("song_url", url);

                song_id = map.get("song_id");

                Redis.put(song_id,set.getString(4));

                genre = set.getString(2);
                user_id = map.get("token_user_id");
                song_url = set.getString(4);
                song_name = set.getString(1);

//                thisSong = new Song(map.get("song_id"),set.getString(4), set.getString(1));


                if (!is_premium) {
                    dbConn = PostgresConfig.getDataSource().getConnection();
                    func = dbConn.prepareStatement("SELECT * FROM get_ad(?);");
                    func.setInt(1, 1);
                    set = func.executeQuery();

                    if (set.next()) {
                        result.put("ad_url", set.getString(1));
                        result.put("ad_photo_url", set.getString(2));
                    }
                }


                ResponseHandler.handleResponse(result.toString(), map.get("queue"), map.get("correlation_id"));

                addToHistory(genre, user_id, song_id, song_url, song_name);
            }
            else{
                func = dbConn.prepareStatement("SELECT * FROM get_song(?);");

                func.setInt(1, Integer.parseInt(map.get("song_id")));
                set = func.executeQuery();
            }



            if (set.next()) {
                String[] urlSplit = set.getString(4).split("/");
                String public_id = "";
                for (int i = 7; i < urlSplit.length; i++) {
                    public_id += urlSplit[i] + "/";
                }
                public_id = public_id.substring(0, public_id.length() - 5);

                String url = "http://138.68.99.91/" + public_id + "/" + bit_rate;

                String totalStreams = set.getString("total_streams");
                int totalStreamsInt = Integer.parseInt(totalStreams) + 1;
                totalStreams = String.valueOf(totalStreamsInt);
//                set.updateString("total_streams", totalStreams);
//                set.updateRow();


                JSONObject result = new JSONObject();

                result.put("song_url", url);

                song_id = map.get("song_id");

                if((totalStreamsInt/Main.elapsed()) > 0.01){
                    Redis.put(song_id,set.getString(4));
                }

                genre = set.getString(2);
                user_id = map.get("token_user_id");
                song_url = set.getString(4);
                song_name = set.getString(1);

//                thisSong = new Song(map.get("song_id"),set.getString(4), set.getString(1));


                if (!is_premium) {
                    dbConn = PostgresConfig.getDataSource().getConnection();
                    func = dbConn.prepareStatement("SELECT * FROM get_ad(?);");
                    func.setInt(1, 1);
                    set = func.executeQuery();

                    if (set.next()) {
                        result.put("ad_url", set.getString(1));
                        result.put("ad_photo_url", set.getString(2));
                    }
                }


                ResponseHandler.handleResponse(result.toString(), map.get("queue"), map.get("correlation_id"));

                addToHistory(genre, user_id, song_id, song_url, song_name);
            } else {
                ResponseHandler.handleError("Cannot find song", STATUSCODES.ENTITYNOTFOUND,
                        map.get("queue"), map.get("correlation_id"));
            }
        } catch (Exception e) {
            ResponseHandler.handleError(e.getMessage(), STATUSCODES.UNKNOWN,
                    map.get("queue"), map.get("correlation_id"));
        }
        finally {
            dbConn.close();
        }


    }

    public void addToHistory(String genre, String user_id, String song_id, String song_url, String song_name) {
        String query = "FOR a IN History FILTER a.genre == @genre and a.user_id == @user_id Return a";
        Map<String, Object> bindVars = new HashMap<>();
        bindVars.put("genre", genre);
        bindVars.put("user_id", user_id);
        ArangoCursor<BaseDocument> cursor = ArangoConfig.arangoDatabase.query(query, bindVars, null, BaseDocument.class);


        if (cursor.hasNext()) {
            cursor.forEachRemaining(aDocument -> {
                BaseDocument myobj = ArangoConfig.arangoDatabase.collection("History").getDocument(aDocument.getKey(), BaseDocument.class);
                Song songToAdd = new Song(song_id, song_url, song_name);

                ArrayList<Song> songs_in_genre = (ArrayList<Song>) myobj.getAttribute("songs");

                JSONArray songsGenre = new JSONArray(songs_in_genre);
                ArrayList<Integer> songIds = new ArrayList<Integer>();

                for (int i = 0; i < songsGenre.length(); i++) {
                    JSONObject eachSong = songsGenre.getJSONObject(i);
                    songIds.add(Integer.parseInt((String) eachSong.get(song_id)));
                }

                boolean songExists = songIds.contains(Integer.parseInt(song_id));
                System.out.println(songIds);
                System.out.println(songExists);

                if (!songExists) {
                    songs_in_genre.add(songToAdd);
                    myobj.addAttribute("songs", songs_in_genre);
                    ArangoConfig.arangoDatabase.collection("History").updateDocument(aDocument.getKey(), myobj);
                }
                return;
            });

        } else {
            ArangoConfig.arangoDatabase.collection("History").insertDocument(SongHistory.createNewSongHistory(user_id, genre, song_name, song_id, song_url));
            return;
        }
    }

    @Override
    public String authorize() throws Exception {
        if (map.get("token_type").equals("artist")) {
            return STATUSCODES.AUTHORIZATION;
        }
        return STATUSCODES.SUCCESS;
    }
}