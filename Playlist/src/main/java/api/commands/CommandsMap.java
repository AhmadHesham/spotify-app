package api.commands;


import cache.Redis;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CommandsMap {

    private static ConcurrentMap<String, Class<?>> cmdMapPlaylists;



    public static void initialize() {
        cmdMapPlaylists = new ConcurrentHashMap<>();
        cmdMapPlaylists.put("create-playlist", CreatePlaylist.class);
        cmdMapPlaylists.put("add-song", SongToPlaylist.class);
        cmdMapPlaylists.put("view-playlists", ViewPlaylists.class);
        cmdMapPlaylists.put("rate-playlist", RatePlaylist.class);
        cmdMapPlaylists.put("view-liked-songs", ViewLikedSongs.class);
        cmdMapPlaylists.put("like-songs", LikeSongs.class);
        cmdMapPlaylists.put("view-albums", ViewAlbums.class);
        cmdMapPlaylists.put("add-albums", AddAlbums.class);
        cmdMapPlaylists.put("change-visibility", ChangeVisibility.class);
        cmdMapPlaylists.put("view-one-playlist", ViewPlaylist.class );
        cmdMapPlaylists.put("remove-liked-album", RemoveLikedAlbum.class);
        cmdMapPlaylists.put("remove-liked-song", RemoveLikedSong.class);
        cmdMapPlaylists.put("delete-playlist", DeletePlaylist.class);
        cmdMapPlaylists.put("delete-song-playlist", RemoveSongFromPlaylist.class);
    }
    
    public static Class<?> queryClass(String cmd, String queue) {
        try {
            if(Redis.hasKey("playlist-freeze") && Redis.get("playlist-freeze").equals("true")){
                return FROZEN.class;
            }

            switch (queue) {
                case "playlist":
                    return cmdMapPlaylists.get(cmd);
                default:
                    return FOUROFOUR.class;
            }
        } catch (Exception e) {
            return FOUROFOUR.class;
        }
    
    }


}
