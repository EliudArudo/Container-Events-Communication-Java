package initialize;

import controllers.RouteControllers;
import env.EnvSetup;
import interfaces.STATUS_TYPE;
import log.Logging;

import static spark.Spark.*;

public class SparkInitialization {
    private static String packageName = "initialize::SparkInitialization";

    public static void setUpRouteListeners() {
        try {
            port(EnvSetup.SparkPort);

            get("/", RouteControllers::indexController);

            post("/task", RouteControllers::requestRouteController);

            notFound(RouteControllers::_404Handler);
        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "setUpRouteListeners", e.getMessage());
        }
    }

}
