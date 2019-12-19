package initialize;

import controllers.RouteControllers;

import static spark.Spark.*;

public class SparkInitialization {
    public static void setUpRouteListeners() {
        get("/", RouteControllers::indexController);

        notFound(RouteControllers::_404Handler);
    }

}
