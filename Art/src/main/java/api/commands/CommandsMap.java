package api.commands;

import cache.Redis;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CommandsMap {
    private static ConcurrentMap<String, Class<?>> cmdMapArt;
    private static ConcurrentMap<String, Class<?>> cmdMapAuth;
    private static ConcurrentMap<String, Class<?>> cmdMapAccounts;
    private static ConcurrentMap<String, Class<?>> cmdMapPlaylists;
    private static ConcurrentMap<String, Class<?>> cmdMapChat;



    public static void initialize() {
    
        cmdMapArt = new ConcurrentHashMap<>();
    
        cmdMapArt.put("create-ad", CreateAd.class);
        cmdMapArt.put("get-ad", GetAd.class);
        cmdMapArt.put("get-ads", GetAds.class);
        cmdMapArt.put("delete-ad", DeleteAd.class);
        cmdMapArt.put("delete-ads", DeleteAds.class);
        cmdMapArt.put("edit-ad", EditAd.class);
        cmdMapArt.put("get-random-ad", GetRandomAd.class);
        cmdMapArt.put("get-ad-id", GetAdId.class);
        cmdMapArt.put("create-album", CreateAlbum.class);
        cmdMapArt.put("get-album", GetAlbum.class);
        cmdMapArt.put("get-albums", GetAlbums.class);
        cmdMapArt.put("edit-album", EditAlbum.class);
        cmdMapArt.put("delete-album", DeleteAlbum.class);
        cmdMapArt.put("rate-artist", RateArtist.class);
        cmdMapArt.put("rate-album", RateAlbum.class);
        cmdMapArt.put("rate-song", RateSong.class);
        cmdMapArt.put("create-song", CreateSong.class);
        cmdMapArt.put("get-song", GetSong.class);
        cmdMapArt.put("get-songs", GetSongs.class);
        cmdMapArt.put("edit-song", EditSong.class);
        cmdMapArt.put("delete-song", DeleteSong.class);


    }
    
    public static Class<?> queryClass(String cmd, String queue) {
        if(Redis.hasKey("art-freeze") && Redis.get("art-freeze").equals("true")){
            return FROZEN.class;
        }

        try {
            switch (queue) {
                case "art":
                    return cmdMapArt.get(cmd);
                default:
                    return FOUROFOUR.class;
            }
        } catch (Exception e) {
            return FOUROFOUR.class;
        }
    
    }


}
