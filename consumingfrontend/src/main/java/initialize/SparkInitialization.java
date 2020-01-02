package initialize;

import controllers.RouteControllers;

import static spark.Spark.*;

public class SparkInitialization {
    public static void setUpRouteListeners() {
        get("/", RouteControllers::indexController);

        post("/task", RouteControllers::requestRouteController);

        notFound(RouteControllers::_404Handler);
    }

}
