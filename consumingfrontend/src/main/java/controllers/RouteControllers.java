package controllers;

import com.google.gson.Gson;
import interfaces.*;
import spark.Request;
import spark.Response;
import tasks.Task;

//https://www.baeldung.com/spark-framework-rest-api
//https://www.petrikainulainen.net/programming/testing/junit-5-tutorial-running-unit-tests-with-maven/


public class RouteControllers {
   public static Object indexController(Request req, Response res) {
       res.type("application/json");
       JSONResponse statusMessage = new JSONResponse("OK");
       return new Gson().toJson(statusMessage);
   }

   public static Object requestRouteController(Request req, Response res) {
       try {
         Object result = Task.taskController(req.body());
         res.type("application/json");

         JSONResponseWithResult sentResult = new JSONResponseWithResult("OK", result);

         return getJSONResponseWithResult(sentResult);

       } catch(Exception e) {
         res.status(500);
         res.type("application/json");

         return getJSONResponse("Server error");
       }
   }

   public static Object _404Handler(Request req, Response res) {
       res.type("application/json");
       return getJSONResponse("Non-existent route and method");
   }

   private static Object getJSONResponse(String responseMessage) {
       JSONResponse statusMessage = new JSONResponse(responseMessage);
       return new Gson().toJson(statusMessage);
   }

   private static Object getJSONResponseWithResult(JSONResponseWithResult result) {
       return new Gson().toJson(result);
   }
}
