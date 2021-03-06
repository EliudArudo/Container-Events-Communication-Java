import env.EnvSetup;
import initialize.*;

public class Main {
    // Github actions v7
    public static void main(String[] args) {
        EnvSetup.fetchEnvVariables();

        DockerAPIInit.initialFetchMyContainerInfo();
        SparkInitialization.setUpRouteListeners();
        RedisInit.initRedis();
    }
}
