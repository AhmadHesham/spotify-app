package models.nosql;

public class Song {
    public String songName;
    public String id;
    public String songURL;
    public String cover_photo;

    public Song(String id, String songURL,String songName, String cover_photo){
        this.id= id;
        this.songURL=songURL;
        this.songName=songName;
        this.cover_photo=cover_photo;
    }
    public Song(String id, String songURL,String songName){
        this.id= id;
        this.songURL=songURL;
        this.songName=songName;
    }


}
