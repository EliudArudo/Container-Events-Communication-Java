package initialize;

import env.EnvSetup;
import redis.clients.jedis.Jedis;

//https://www.baeldung.com/jedis-java-redis-client-library

public class RedisInit {

    private static Jedis RedisClient;

    public RedisInit(){}

    public static void initRedis() {
        String host = EnvSetup.RedisKeys.REDIS_HOST;
        int port = Integer.parseInt(EnvSetup.RedisKeys.REDIS_PORT);

        RedisClient = new Jedis(host, port);
    }

    public static Jedis getRedisClient() {
        return RedisClient;
    }

}
