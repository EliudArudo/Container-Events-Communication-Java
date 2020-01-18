package env;

import interfaces.RedisEnvInterface;

public class EnvSetup {
   private static String REDIS_HOST = "REDIS_HOST";
   private static String REDIS_PORT = "REDIS_PORT";

   private static String PORT = "PORT";

    public static String EVENT_SERVICE_EVENT = "EVENT_SERVICE_EVENT";

    public static String CONSUMING_SERVICE_EVENT = "CONSUMING_SERVICE_EVENT";


    public static RedisEnvInterface RedisKeys = new RedisEnvInterface("localhost", "6379");
    public static int SparkPort =  4000;
    public static String EventServiceEvent = "Event_Service";
    public static String ConsumingServiceEvent = "Consuming_Service";

    public EnvSetup(){}

    public static void fetchEnvVariables() {
        String redisHostFromEnv = System.getenv(REDIS_HOST);
        String redisPortFromEnv = System.getenv(REDIS_PORT);
        String sparkPortFromEnv = System.getenv(PORT);
        String eventServiceEventFromEnv = System.getenv(EVENT_SERVICE_EVENT);
        String consumingServiceEventFromEnv = System.getenv(CONSUMING_SERVICE_EVENT);

        if (redisHostFromEnv.length() > 0 && redisPortFromEnv.length() > 0) {
            RedisKeys.REDIS_HOST = redisHostFromEnv;
            RedisKeys.REDIS_PORT = redisPortFromEnv;
        }

        SparkPort = sparkPortFromEnv.length() > 0? Integer.parseInt(sparkPortFromEnv) : SparkPort;
        EventServiceEvent = eventServiceEventFromEnv.length() > 0? eventServiceEventFromEnv : EventServiceEvent;
        ConsumingServiceEvent = consumingServiceEventFromEnv.length() > 0? consumingServiceEventFromEnv : ConsumingServiceEvent;
    }
}
