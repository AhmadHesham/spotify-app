package cache;

import redis.clients.jedis.Jedis;

public class Redis {
    public static Jedis jedis = new Jedis("localhost");

    public static void put(String key, String value){
        jedis.set(key, value);
    }

    public static String get(String key){
        return jedis.get(key);
    }

    public static boolean hasKey(String key){
        return jedis.exists(key);
    }
    public static void main(String[] args) {
        put("x","y");
//        put("x","88");

        System.out.println(get("x"));
    }
}
