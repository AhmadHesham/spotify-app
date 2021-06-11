package models.nosql;

import java.util.ArrayList;

public class LikedAlbums {
    String userId;
    ArrayList<Album> albums;

    public LikedAlbums(String userId){
        this.userId=userId;
        albums= new ArrayList<Album>();
    }

    public static LikedAlbums createLikedAlbums (String userId, Album a){
        LikedAlbums l = new LikedAlbums(userId);
        l.albums.add(a);
        return l;
    }


}
