import env.EnvSetup;
import initialize.*;

// Todo - Fix Logging then START testing!!!

public class Main {
    public static void main(String[] args) {
        EnvSetup.fetchEnvVariables();
        SparkInitialization.setUpRouteListeners();
        RedisInit.initRedis();
    }
}
