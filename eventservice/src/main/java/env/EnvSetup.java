package env;

import interfaces.RedisEnvInterface;
import interfaces.STATUS_TYPE;
import log.Logging;

public class EnvSetup {
   private static String packageName = "env::EnvSetup";

   private static String REDIS_HOST = "REDIS_HOST";
   private static String REDIS_PORT = "REDIS_PORT";

    public static String EVENT_SERVICE_EVENT_ENV = "EVENT_SERVICE_EVENT";

    public static String CONSUMING_SERVICE_EVENT_ENV = "CONSUMING_SERVICE_EVENT";


    public static RedisEnvInterface RedisKeys = new RedisEnvInterface("localhost", "6379");
    public static String EventServiceEvent = "Event_Service";
    public static String ConsumingServiceEvent = "Consuming_Service";

    public EnvSetup(){}

    public static void fetchEnvVariables() {

        try {
            String redisHostFromEnv = System.getenv(REDIS_HOST);
            String redisPortFromEnv = System.getenv(REDIS_PORT);

            String eventServiceEventFromEnv = System.getenv(EVENT_SERVICE_EVENT_ENV);
            String consumingServiceEventFromEnv = System.getenv(CONSUMING_SERVICE_EVENT_ENV);

            if (redisHostFromEnv.length() > 0 && redisPortFromEnv.length() > 0) {
                RedisKeys.REDIS_HOST = redisHostFromEnv;
                RedisKeys.REDIS_PORT = redisPortFromEnv;
            }

            EventServiceEvent = eventServiceEventFromEnv.length() > 0? eventServiceEventFromEnv : EventServiceEvent;
            ConsumingServiceEvent = consumingServiceEventFromEnv.length() > 0? consumingServiceEventFromEnv : ConsumingServiceEvent;

        } catch(Exception e) {
          Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "fetchEnvVariables", e.getMessage());
        }
    }
}
