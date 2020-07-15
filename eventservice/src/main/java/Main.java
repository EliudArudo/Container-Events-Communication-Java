import env.EnvSetup;
import initialize.DockerAPIInit;
import initialize.MongoDBInit;
import initialize.RedisInit;

public class Main {
    // Github actions v1
    public static void main(String[] args) {
        EnvSetup.fetchEnvVariables();

        DockerAPIInit.initialFetchMyContainerInfo();
        MongoDBInit.initialiseConnection();
        RedisInit.initRedis();

    }
}
