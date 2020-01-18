package initialize;

import controllers.RouteControllers;
import env.EnvSetup;

import static spark.Spark.*;

public class SparkInitialization {
    public static void setUpRouteListeners() {
        port(EnvSetup.SparkPort);

        get("/", RouteControllers::indexController);

        post("/task", RouteControllers::requestRouteController);

        notFound(RouteControllers::_404Handler);
    }

}
