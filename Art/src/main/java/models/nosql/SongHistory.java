package models.nosql;

import java.util.ArrayList;
import java.util.UUID;

public class SongHistory {
    String id;
    String user_id;
    String genre;
    ArrayList<Song> songs;

    public SongHistory(String user_id, String genre, ArrayList<Song> songs) {
        this.id = UUID.randomUUID().toString();
        this.user_id = user_id;
        this.genre = genre;
        this.songs = songs;
    }

    public static SongHistory createNewSongHistory(String user_id, String genre, String song_name, String song_id,String song_url){
        Song s = new Song(song_id, song_url, song_name);
        ArrayList historyArray = new ArrayList();
        historyArray.add(s);
        return new SongHistory(user_id, genre, historyArray);
    }
}
