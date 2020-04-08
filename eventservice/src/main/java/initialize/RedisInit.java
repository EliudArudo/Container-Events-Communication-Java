package initialize;

import controllers.RedisController;
import dockerapi.ContainerInfo;
import env.EnvSetup;
import interfaces.STATUS_TYPE;
import log.Logging;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

//https://www.baeldung.com/jedis-java-redis-client-library

public class RedisInit {
    private static String packageName = "initialize::DockerAPIInit";

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
            String consumingService = EnvSetup.ConsumingServiceEvent;

            subscriber = new Jedis(host, port);

            subscriber.subscribe(new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    ContainerInfo containerInfo = DockerAPIInit.getContainerInfoInstance();
                    RedisController.redisControllerSetup(message, containerInfo);
                }
            }, consumingService);
        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "setUpRedisSubscription", e.getMessage());
        }
    }
}
