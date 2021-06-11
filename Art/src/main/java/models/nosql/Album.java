package models.nosql;

public class Album {
    String albumName;
    String id;
    String albumURL;
    String cover_photo;
    
    public Album(String id, String albumURL, String albumName, String cover_photo){
        this.id= id;
        this.albumURL=albumURL;
        this.albumName=albumName;
        this.cover_photo=cover_photo;
    }
}
