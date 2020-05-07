package env;

import interfaces.MongoDBEnvInterface;
import interfaces.RedisEnvInterface;
import interfaces.STATUS_TYPE;
import log.Logging;

public class EnvSetup {
   private static String packageName = "env::EnvSetup";

   private static String REDIS_HOST = "REDIS_HOST";
   private static String REDIS_PORT = "REDIS_PORT";

    public static String EVENT_SERVICE_EVENT_ENV = "EVENT_SERVICE_EVENT";

    public static String CONSUMING_SERVICE_EVENT_ENV = "CONSUMING_SERVICE_EVENT";

    public static String MONGO_URI_ENV = "MONGOURI";
    public static String MONGO_PORT_ENV = "MONGOPORT";
    public static String MONGO_DATABASE_ENV = "MONGODATABASE";

    public static RedisEnvInterface RedisKeys = new RedisEnvInterface("localhost", "6379");
    public static String EventServiceEvent = "Event_Service";
    public static String ConsumingServiceEvent = "Consuming_Service";

    public static MongoDBEnvInterface MongoDBKeys = new MongoDBEnvInterface("localhost", 27017, "test");

    public EnvSetup(){}

    public static void fetchEnvVariables() {

        try {
            String redisHostFromEnv = System.getenv(REDIS_HOST);
            String redisPortFromEnv = System.getenv(REDIS_PORT);

            String mongoUriFromEnv = System.getenv(MONGO_URI_ENV);
            String mongoPortFromEnv = System.getenv(MONGO_PORT_ENV);
            String mongoDatabaseFromEnv = System.getenv(MONGO_DATABASE_ENV);

            String eventServiceEventFromEnv = System.getenv(EVENT_SERVICE_EVENT_ENV);
            String consumingServiceEventFromEnv = System.getenv(CONSUMING_SERVICE_EVENT_ENV);

            if (redisHostFromEnv.length() > 0 && redisPortFromEnv.length() > 0) {
                RedisKeys.REDIS_HOST = redisHostFromEnv;
                RedisKeys.REDIS_PORT = redisPortFromEnv;
            }

            if(mongoUriFromEnv.length() > 0) {
                int convertedPort = Integer.parseInt(mongoPortFromEnv);
                MongoDBKeys = new MongoDBEnvInterface(mongoUriFromEnv, convertedPort, mongoDatabaseFromEnv);
            }

            EventServiceEvent = eventServiceEventFromEnv.length() > 0? eventServiceEventFromEnv : EventServiceEvent;
            ConsumingServiceEvent = consumingServiceEventFromEnv.length() > 0? consumingServiceEventFromEnv : ConsumingServiceEvent;

        } catch(Exception e) {
          Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "fetchEnvVariables", e.getMessage());
        }
    }
}
