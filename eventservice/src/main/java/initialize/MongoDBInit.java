package initialize;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import env.EnvSetup;
import interfaces.MongoDBEnvInterface;
import interfaces.STATUS_TYPE;
import log.Logging;

public class MongoDBInit {
    private static String packageName = "initialize::MongoDBInit";

    private static MongoClient mongoClient;

    public static void initialiseConnection() {
        try {
            MongoDBEnvInterface mongoKeys = EnvSetup.MongoDBKeys;
            String mongoURI = mongoKeys.getFullURI();

             mongoClient = new MongoClient(new MongoClientURI(mongoURI));

        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "setUpRedisPublisher", e.getMessage());
        }
    }

    public static MongoClient getMongoClient() {
        return mongoClient;
    }
}
