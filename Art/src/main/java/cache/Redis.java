package cache;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.api.RBucket;
import org.redisson.config.Config;
import redis.clients.jedis.Jedis;


public class Redis {


    public static Jedis jedis = new Jedis("redis", 6379, 604800);


    public static void put(String key, String value){
        jedis.set(key, value);
    }

    public static String get(String key){
        return jedis.get(key);
    }

    public static boolean hasKey(String key){
        return jedis.exists(key);
    }
    public static void delete(String key){
        jedis.del(key);
    }
}