package databaseops;

import com.google.gson.Gson;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import dockerapi.DockerAPI;
import env.EnvSetup;
import initialize.MongoDBInit;
import interfaces.*;
import log.Logging;
import models.Request;
import models.Response;
import models.Task;
import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import util.Util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

// http://jongo.org/ - MongoDB Driver
public class MongoDBOps {
    private static String packageName = "databaseops::MongoDBOps";

    private static String getCurrentISOTimeString() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);

        return df.format(new Date());
    }

    private static DB getDB() {
        try {
            MongoClient mongoClient = MongoDBInit.getMongoClient();
            String dbName = EnvSetup.MongoDBKeys.database;

            return mongoClient.getDB(dbName);

        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "getDB", e.getMessage());
            return null;
        }
    }

    private static MongoCollection getTaskCollection () {
        try {
            DB db = getDB();

            Jongo jongo = new Jongo(db);
            return jongo.getCollection("tasks");

        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "getTaskCollection", e.getMessage());
            return null;
        }
    }

    private static MongoCollection getRequestsCollection () {
        try {
            DB db = getDB();;

            Jongo jongo = new Jongo(db);
            return jongo.getCollection("requests");

        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "getTaskCollection", e.getMessage());
            return null;
        }
    }

    private static MongoCollection getResponsesCollection () {
        try {
            DB db = getDB();;

            Jongo jongo = new Jongo(db);
            return jongo.getCollection("responses");

        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "getTaskCollection", e.getMessage());
            return null;
        }
    }

    public static String getExistingRequestDocumentID(String request) {
        String existingRequestDocumentID = null;
        try {

            Map<String, String> requestQuery = new HashMap();
            requestQuery.put("request", request);

            String jsonRequestQuery = new Gson().toJson(requestQuery);

            MongoCollection requestCollection = getRequestsCollection();

            Request existingRequestDocument = requestCollection.findOne(jsonRequestQuery).as(Request.class);

            if(existingRequestDocument != null) {
                existingRequestDocumentID = existingRequestDocument._id;
            }

        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "getExistingRequestDocumentID", e.getMessage());
        } finally {
            return existingRequestDocumentID;
        }
    }

    public static Task getExistingTask(EventInterface task) {
        Task existingRecord = null;

        try {
            Map<String, String> taskQuery = new HashMap();
            taskQuery.put("fromContainerService", task.service);
            taskQuery.put("fromTask", task.task.toString());
            taskQuery.put("fromSubtask", task.subtask.toString());

            String requestBodyId = getExistingRequestDocumentID(task.requestBody);

            taskQuery.put("requestBodyId", requestBodyId);

            if(requestBodyId.length() > 0) {
                String jsonTaskQuery = new Gson().toJson(taskQuery);

                MongoCollection tasksCollection = getTaskCollection();
                existingRecord = tasksCollection.findOne(jsonTaskQuery).as(Task.class);
            }

        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "getExistingTask", e.getMessage());
        } finally {
            return existingRecord;
        }
    }

    public static InitialisedRecordInfoInterface getNewParsedTask(Task mongoDBTask, ContainerInfoInterface selectedContainerInfo) {

        InitialisedRecordInfoInterface parsedTask = new InitialisedRecordInfoInterface();

       try {
           parsedTask.containerId = mongoDBTask.fromContainerId;
           parsedTask.containerService = mongoDBTask.fromContainerService;
           parsedTask.recordId = mongoDBTask._id;
           parsedTask.task = mongoDBTask.task == "NUMBER"? TASK_TYPE.NUMBER : TASK_TYPE.STRING;
           parsedTask.subtask = mongoDBTask.subtask == "ADD"? SUB_TASK_TYPE.ADD :
                   mongoDBTask.subtask == "SUBTRACT"? SUB_TASK_TYPE.SUBTRACT :
                           mongoDBTask.subtask == "MULTIPLY"? SUB_TASK_TYPE.MULTIPLY :
                                   mongoDBTask.subtask == "DIVIDE"? SUB_TASK_TYPE.DIVIDE : null;
           parsedTask.serviceContainerId = mongoDBTask.serviceContainerId;
           parsedTask.serviceContainerService = mongoDBTask.serviceContainerService;
           parsedTask.chosenContainerId = selectedContainerInfo.id;
           parsedTask.chosenContainerService = selectedContainerInfo.service;

           String toResponseBodyId = mongoDBTask.toResponseBodyId;
           MongoCollection responseCollection = getResponsesCollection();
           ObjectId responseId = new ObjectId(toResponseBodyId);

           Response response = responseCollection.findOne(responseId).as(Response.class);

           if(response.response.length() > 0)
               parsedTask.responseBody = response.response;

       } catch(Exception e) {
           Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "getNewParsedTask", e.getMessage());
       } finally {
           return parsedTask;
       }
    }

    public static InitialisedRecordInfoInterface getExistingParsedTask(Task mongoDBTask) {
        InitialisedRecordInfoInterface parsedTask = new InitialisedRecordInfoInterface();

        try {
            String toResponseBodyId = mongoDBTask.toResponseBodyId;

            MongoCollection responseCollection = getResponsesCollection();

            Map<String, String> responseMap = new HashMap();
            responseMap.put("_id", toResponseBodyId);

            String jsonResponseQuery = new Gson().toJson(responseMap);

            Response response = responseCollection.findOne(jsonResponseQuery).as(Response.class);

            parsedTask.containerId = mongoDBTask.fromContainerId;
            parsedTask.containerService = mongoDBTask.fromContainerService;
            parsedTask.recordId = mongoDBTask._id;
            parsedTask.task = mongoDBTask.task == "NUMBER"? TASK_TYPE.NUMBER : TASK_TYPE.STRING;
            parsedTask.subtask = mongoDBTask.subtask == "ADD"? SUB_TASK_TYPE.ADD :
              mongoDBTask.subtask == "SUBTRACT"? SUB_TASK_TYPE.SUBTRACT :
              mongoDBTask.subtask == "MULTIPLY"? SUB_TASK_TYPE.MULTIPLY :
              mongoDBTask.subtask == "DIVIDE"? SUB_TASK_TYPE.DIVIDE : null;

            parsedTask.serviceContainerId = mongoDBTask.serviceContainerId;
            parsedTask.serviceContainerService = mongoDBTask.serviceContainerService;
            parsedTask.responseBody = response.response;

        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "getExistingParsedTask", e.getMessage());
        } finally {
            return parsedTask;
        }
    }

    public static String saveNewRequestAndGetID(String requestBody) {
        String requestId = null;
        try {

            Request newRequest = new Request();
            newRequest.request = requestBody;

            MongoCollection requestsCollection = getRequestsCollection();
            requestsCollection.save(newRequest);

            MongoCollection requestCollection = getRequestsCollection();
            Map<String, String> requestQueryMap = new HashMap();
            requestQueryMap.put("request", requestBody);
            String jsonRequestQuery = new Gson().toJson(requestQueryMap);
            Request request = requestCollection.findOne(jsonRequestQuery).as(Request.class);

            requestId = request._id;

        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "saveNewRequestAndGetID", e.getMessage());
        } finally {
            return requestId;
        }
    }

    public static String saveNewResponseAndGetID(String responseBody) {
        String responseId = null;
        try {

            Response newResponse = new Response();
            newResponse.response = responseBody;

            MongoCollection responseCollection = getResponsesCollection();
            responseCollection.save(newResponse);

            Map<String, String> responseQueryMap = new HashMap();
            responseQueryMap.put("response", responseBody);
            String jsonResponseQuery = new Gson().toJson(responseQueryMap);
            Response response = responseCollection.findOne(jsonResponseQuery).as(Response.class);

            responseId = response._id;

        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "saveNewRequestAndGetID", e.getMessage());
        } finally {
            return responseId;
        }
    }

    public static EventInterface getParsedResponse(EventInterface funcResponse, Task oldTask) {
        EventInterface response = new EventInterface();

        response.requestId = oldTask.fromRequestId;
        response.containerId = oldTask.fromContainerId;
        response.service = oldTask.fromContainerService;
        response.responseBody = funcResponse.responseBody;

        return response;

    }

    public static void completeRecordInDB(EventInterface funcResponse, String receivedTime, String responseBodyId) {
      try {

          String fromSentTime = getCurrentISOTimeString();

          MongoCollection taskCollection = getTaskCollection();
          ObjectId taskId = new ObjectId(funcResponse.recordId);

          Task task = taskCollection.findOne(taskId).as(Task.class);
          task.toReceivedTime = receivedTime;
          task.toResponseBodyId = responseBodyId;
          task.fromSentTime = fromSentTime;

          taskCollection.save(task);

      } catch(Exception e) {
          Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "completeRecordInDB", e.getMessage());
      }
    }

    public static InitialisedRecordInfoInterface recordNewInitialisedTaskWithRequestId(EventInterface funcTask, String requestBodyId) {
        InitialisedRecordInfoInterface parsedTask = new InitialisedRecordInfoInterface();
        try {

            String fromRequestId = funcTask.requestId;
            String fromContainerId = funcTask.containerId;
            String fromContainerService = funcTask.service;
            String fromReceivedTime = getCurrentISOTimeString();
            String task = funcTask.toString();
            String subtask = funcTask.subtask.toString();


            ContainerInfoInterface selectedContainerInfo = Util.getSelectedContainerIdAndService(funcTask);

            String toContainerId = selectedContainerInfo.id;
            String toContainerService = selectedContainerInfo.service;

            ContainerInfoInterface myContainerInfo = DockerAPI.fetchOfflineContainerInfo();

            String serviceContainerId = myContainerInfo.id;
            String serviceContainerService = myContainerInfo.service;

            Task newInitTaskRecord = new Task();
            newInitTaskRecord.fromRequestId = fromRequestId;
            newInitTaskRecord.fromContainerId = fromContainerId;
            newInitTaskRecord.fromContainerService = fromContainerService;
            newInitTaskRecord.fromReceivedTime = fromReceivedTime;
            newInitTaskRecord.task = task;
            newInitTaskRecord.subtask = subtask;
            newInitTaskRecord.requestBodyId = requestBodyId;
            newInitTaskRecord.toContainerId = toContainerId;
            newInitTaskRecord.toContainerService = toContainerService;
            newInitTaskRecord.serviceContainerId = serviceContainerId;
            newInitTaskRecord.serviceContainerService = serviceContainerService;

            MongoCollection tasksCollection = getTaskCollection();
            tasksCollection.save(newInitTaskRecord);

            Map<String, String> taskQueryMap = new HashMap();
            taskQueryMap.put("requestBodyId", requestBodyId);
            String jsonTaskQuery = new Gson().toJson(taskQueryMap);

            Task newTask = tasksCollection.findOne(jsonTaskQuery).as(Task.class);
            parsedTask = getNewParsedTask(newTask, selectedContainerInfo);

        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "recordNewInitialisedTaskWithRequestId", e.getMessage());
        } finally {
          return parsedTask;
        }
    }

    public static InitialisedRecordInfoInterface recordNewTaskAndRequest(EventInterface funcTask) {
        InitialisedRecordInfoInterface initialisedInfo = new InitialisedRecordInfoInterface();
        try {
            String requestBodyId = saveNewRequestAndGetID(funcTask.requestBody);

            initialisedInfo = recordNewInitialisedTaskWithRequestId(funcTask, requestBodyId);

            initialisedInfo.requestBody = funcTask.requestBody;

        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "recordNewTaskAndRequest", e.getMessage());
        } finally {
           return initialisedInfo;
        }
    }

    public static InitialisedRecordInfoInterface recordNewTaskInDB(EventInterface task) {

        Task existingTask = getExistingTask(task);

        if(existingTask != null) {
            InitialisedRecordInfoInterface parsedTask = getExistingParsedTask(existingTask);

            parsedTask.existing = true;

            return parsedTask;
        }

        InitialisedRecordInfoInterface initRecordInfo = recordNewTaskAndRequest(task);
         return initRecordInfo;
    }

    public static EventInterface completeExistingTaskRecordInDB(EventInterface funcResponse) {
        EventInterface response = null;
       try {
           MongoCollection responseCollection = getResponsesCollection();
           Map<String, String> responseQueryMap = new HashMap();
           String jsonResponseQuery = new Gson().toJson(responseQueryMap);
           responseQueryMap.put("response", funcResponse.requestBody);

           Response preexistingResponse = responseCollection.findOne(jsonResponseQuery).as(Response.class);

           if(preexistingResponse.response.length() == 0) {
              String toReceivedTime = getCurrentISOTimeString();
              String toResponseBodyId = saveNewResponseAndGetID(funcResponse.responseBody);

              completeRecordInDB(funcResponse, toReceivedTime, toResponseBodyId);
           }

           ObjectId recordId = new ObjectId(funcResponse.recordId);
           MongoCollection taskCollection = getTaskCollection();
           Task task = taskCollection.findOne(recordId).as(Task.class);
           response = getParsedResponse(funcResponse, task);

       } catch(Exception e) {

       } finally {
           return response;
       }
    }
}
