import env.EnvSetup;
import initialize.DockerAPIInit;
import initialize.MongoDBInit;
import initialize.RedisInit;

public class Main {
    public static void main(String[] args) {
        EnvSetup.fetchEnvVariables();

        DockerAPIInit.initialFetchMyContainerInfo();
        RedisInit.initRedis();
        MongoDBInit.initialiseConnection();
    }
}
