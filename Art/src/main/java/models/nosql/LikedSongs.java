package models.nosql;

import java.util.ArrayList;

public class LikedSongs {
    String userId;
    ArrayList<Song> likes;

    public LikedSongs(String userId){
        this.userId=userId;
        this.likes= new ArrayList<Song>();
    }

    public static LikedSongs createLikedSongs (String userId, Song s){
        LikedSongs l = new LikedSongs(userId);
        l.likes.add(s);
        return l;
    }

}
