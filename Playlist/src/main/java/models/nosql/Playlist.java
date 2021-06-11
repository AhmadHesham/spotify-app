package models.nosql;
import java.util.ArrayList;
import java.util.UUID;

public class Playlist {
    String id;
    String userId;
    float avgRating;
    int number_of_ratings;
    String playlistName;
    String playlistURL;
    ArrayList<Song> songs;
    Boolean visible;

    public Playlist(String userId,float avgRating,int number_of_ratings,String playlistName, String playlistURL, Boolean visible){
        id= UUID.randomUUID().toString();
        this.avgRating= avgRating;
        this.number_of_ratings = number_of_ratings;
        this.userId=userId;
        this.playlistName= playlistName;
        this.playlistURL= playlistURL+id;
        this.songs= new ArrayList<Song>();
        this.visible=visible;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public float getAvgRating() {
        return avgRating;
    }
    public int getNumber_of_ratings() {
        return number_of_ratings;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public String getPlaylistURL() {
        return playlistURL;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }
    public Boolean getVisible() {
        return visible;
    }

    public static Playlist createPlaylist(String playlistName, String userId, String visibility){
        return new Playlist(userId,0,0,playlistName, "https://open.spotify.com/playlist/", Boolean.valueOf(visibility));
    }

}
