import env.EnvSetup;
import initialize.*;

// Todo - Fix Logging then START testing!!!

public class Main {
    public static void main(String[] args) {
        EnvSetup.fetchEnvVariables();

        DockerAPIInit.initialFetchMyContainerInfo();
        SparkInitialization.setUpRouteListeners();
        RedisInit.initRedis();
    }
}
