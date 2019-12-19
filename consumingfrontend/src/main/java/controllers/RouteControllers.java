package controllers;

import com.google.gson.Gson;
import interfaces.JSONResponse;
import spark.Request;
import spark.Response;

//https://www.baeldung.com/spark-framework-rest-api

public class RouteControllers {
   public static Object indexController(Request req, Response res) {
       res.type("application/json");
       JSONResponse statusMessage = new JSONResponse("OK");
       return new Gson().toJson(statusMessage);
   }

   public static Object _404Handler(Request req, Response res) {
       res.type("application/json");
       JSONResponse statusMessage = new JSONResponse("Non-existent route and method");
       return new Gson().toJson(statusMessage);
   }
}
