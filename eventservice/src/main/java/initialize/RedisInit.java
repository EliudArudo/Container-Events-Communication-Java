package initialize;

import controllers.RedisController;
import dockerapi.DockerAPI;
import env.EnvSetup;
import interfaces.STATUS_TYPE;
import log.Logging;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

//https://www.baeldung.com/jedis-java-redis-client-library

public class RedisInit {
    private static String packageName = "initialize::RedisInit";

    private static Jedis publisher;
    private static Jedis subscriber;

    public RedisInit(){}

    public static void initRedis() {
        String host = EnvSetup.RedisKeys.REDIS_HOST;
        int port = Integer.parseInt(EnvSetup.RedisKeys.REDIS_PORT);

        setUpRedisPublisher(host, port);
        setUpRedisSubscription(host, port);
    }

    public static Jedis getRedisSubscriber() {
        return subscriber;
    }

    public static Jedis getRedisPublisher() {
        return publisher;
    }

    public static void setUpRedisPublisher(String host, int port) {
        try {
            publisher = new Jedis(host, port);
        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "setUpRedisPublisher", e.getMessage());
        }
    }

    public static void setUpRedisSubscription(String host, int port) {

        try {
            String eventService = EnvSetup.EventServiceEvent;

            subscriber = new Jedis(host, port);

            Logging.logStatusFileMessage(STATUS_TYPE.Success, packageName, "setUpRedisSubscription", "Redis connection successful");

            subscriber.subscribe(new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    DockerAPI dockerAPI = DockerAPIInit.getContainerInfoInstance();
                    RedisController.redisControllerSetup(message, dockerAPI);
                }
            }, eventService);

        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "setUpRedisSubscription", e.getMessage());
        }
    }
}
