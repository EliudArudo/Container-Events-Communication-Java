import env.EnvSetup;
import initialize.*;

public class Main {
    public static void main(String[] args) {
        EnvSetup.fetchEnvVariables();

        DockerAPIInit.initialFetchMyContainerInfo();
        SparkInitialization.setUpRouteListeners();
        RedisInit.initRedis();
    }
}
