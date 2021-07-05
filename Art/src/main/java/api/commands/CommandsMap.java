package api.commands;

import cache.Redis;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CommandsMap {
    private static ConcurrentMap<String, Class<?>> cmdMapArt = new ConcurrentHashMap<>();
    private static Properties prop = new Properties();

    public static void initialize() throws Exception {
        try {
            prop.load(CommandsMap.class.getClassLoader().getResourceAsStream("ArtCommands.properties"));
            Enumeration<?> propNames = prop.propertyNames();
            while (propNames.hasMoreElements()) {
                Object current = propNames.nextElement();
                cmdMapArt.put(current.toString(),
                        Class.forName("api.commands." + prop.get(current.toString()).toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//
//    public static void initialize() {
//
//        cmdMapArt = new ConcurrentHashMap<>();
//
//        cmdMapArt.put("create-ad", CreateAd.class);
//        cmdMapArt.put("get-ad", GetAd.class);
//        cmdMapArt.put("get-ads", GetAds.class);
//        cmdMapArt.put("delete-ad", DeleteAd.class);
//        cmdMapArt.put("delete-ads", DeleteAds.class);
//        cmdMapArt.put("edit-ad", EditAd.class);
//        cmdMapArt.put("get-random-ad", GetRandomAd.class);
//        cmdMapArt.put("get-ad-id", GetAdId.class);
//        cmdMapArt.put("create-album", CreateAlbum.class);
//        cmdMapArt.put("get-album", GetAlbum.class);
//        cmdMapArt.put("get-albums", GetAlbums.class);
//        cmdMapArt.put("edit-album", EditAlbum.class);
//        cmdMapArt.put("delete-album", DeleteAlbum.class);
//        cmdMapArt.put("rate-artist", RateArtist.class);
//        cmdMapArt.put("rate-album", RateAlbum.class);
//        cmdMapArt.put("rate-song", RateSong.class);
//        cmdMapArt.put("create-song", CreateSong.class);
//        cmdMapArt.put("get-song", GetSong.class);
//        cmdMapArt.put("get-songs", GetSongs.class);
//        cmdMapArt.put("edit-song", EditSong.class);
//        cmdMapArt.put("delete-song", DeleteSong.class);
//
//
//    }
    
    public static Class<?> queryClass(String cmd, String queue) {
//        System.out.println(Redis.get("art-freeze").equals("true"));
        if(Redis.hasKey("art-freeze") && Redis.get("art-freeze").equals("true")){
            return FROZEN.class;
        }

        try {
            System.out.println(cmd);
            System.out.println(queue);
            System.out.println(cmdMapArt);
            switch (queue) {
                case "art":
                    return cmdMapArt.get(cmd);
                default:
                    return FOUROFOUR.class;
            }
        } catch (Exception e) {
            System.out.println("");
            return FOUROFOUR.class;
        }
    
    }

    public static void replace(String key, Class<?> cls, String fileName) {
        if (cmdMapArt.containsKey(key)) {
            cmdMapArt.replace(key, cls);
        } else {
            cmdMapArt.put(key, cls);
            prop.setProperty(key, fileName);
            try {
                prop.store(new FileOutputStream("Art/src/main/resources/ArtCommands.properties"), null);
            } catch (IOException e) {
                System.out.println("Error in updating .properties file");
            }
        }
    }


}
