package databaseops;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.jongo.*;
import org.jongo.marshall.jackson.JacksonMapper;
import org.jongo.marshall.jackson.configuration.MapperModifier;
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

            Mapper mapper = new JacksonMapper.Builder().addModifier(new MapperModifier() {
                public void modify(ObjectMapper mapper) {
                    mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
                }
            }).build();

            Jongo jongo = new Jongo(db, mapper);
            return jongo.getCollection("tasks");

        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "getTaskCollection", e.getMessage());
            return null;
        }
    }

    private static MongoCollection getRequestsCollection () {
        try {
            DB db = getDB();

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
                existingRequestDocumentID = existingRequestDocument.getId();
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
            taskQuery.put("task", task.task.toString());
            taskQuery.put("subtask", task.subtask.toString());

            if(task.requestBody != null  && task.requestBody.length() > 0) {
                String requestBodyId = getExistingRequestDocumentID(task.requestBody);

                taskQuery.put("requestBodyId", requestBodyId);
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
           parsedTask.recordId = mongoDBTask.getId();
           /* NOTE: Use '.equals' to compare strings, NOT '=='*/
           parsedTask.task = mongoDBTask.task.equals("NUMBER")? TASK_TYPE.NUMBER : TASK_TYPE.STRING;
           parsedTask.subtask = mongoDBTask.subtask.equals("ADD")? SUB_TASK_TYPE.ADD :
                   mongoDBTask.subtask.equals("SUBTRACT")? SUB_TASK_TYPE.SUBTRACT :
                           mongoDBTask.subtask.equals("MULTIPLY")? SUB_TASK_TYPE.MULTIPLY :
                                   mongoDBTask.subtask.equals("DIVIDE")? SUB_TASK_TYPE.DIVIDE : null;



           parsedTask.serviceContainerId = mongoDBTask.serviceContainerId;
           parsedTask.serviceContainerService = mongoDBTask.serviceContainerService;
           parsedTask.chosenContainerId = selectedContainerInfo.id;
           parsedTask.chosenContainerService = selectedContainerInfo.service;


           if(mongoDBTask.toResponseBodyId != null && mongoDBTask.toResponseBodyId.length() > 0) {

               String toResponseBodyId = mongoDBTask.toResponseBodyId;
               MongoCollection responseCollection = getResponsesCollection();

               Response response = responseCollection.findOne(Oid.withOid(toResponseBodyId)).as(Response.class);

               parsedTask.responseBody = response.response;
           }


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

            Response response = responseCollection.findOne(Oid.withOid(toResponseBodyId)).as(Response.class);

            parsedTask.containerId = mongoDBTask.fromContainerId;
            parsedTask.containerService = mongoDBTask.fromContainerService;
            parsedTask.recordId = mongoDBTask.getId();
            parsedTask.task = mongoDBTask.task.equals("NUMBER")? TASK_TYPE.NUMBER : TASK_TYPE.STRING;
            parsedTask.subtask = mongoDBTask.subtask == "ADD"? SUB_TASK_TYPE.ADD :
              mongoDBTask.subtask.equals("SUBTRACT")? SUB_TASK_TYPE.SUBTRACT :
              mongoDBTask.subtask.equals("MULTIPLY")? SUB_TASK_TYPE.MULTIPLY :
              mongoDBTask.subtask.equals("DIVIDE")? SUB_TASK_TYPE.DIVIDE : null;

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

            Request request = new Request();
            request.request = requestBody;

            MongoCollection requestsCollection = getRequestsCollection();
            String newRequest = new Gson().toJson(request);
            requestsCollection.insert(newRequest);

            MongoCollection requestCollection = getRequestsCollection();
            Map<String, String> requestQueryMap = new HashMap();
            requestQueryMap.put("request", requestBody);
            String jsonRequestQuery = new Gson().toJson(requestQueryMap);
            Request foundRequest = requestCollection.findOne(jsonRequestQuery).as(Request.class);

            requestId = foundRequest.getId();

        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "saveNewRequestAndGetID", e.getMessage());
        } finally {
            return requestId;
        }
    }

    public static String saveNewResponseAndGetID(String responseBody) {
        String responseId = null;
        try {

            Response response = new Response();
            response.response = responseBody;

            MongoCollection responseCollection = getResponsesCollection();
            String newResponse = new Gson().toJson(response);
            responseCollection.insert(newResponse);

            Map<String, String> responseQueryMap = new HashMap();
            responseQueryMap.put("response", responseBody);
            String jsonResponseQuery = new Gson().toJson(responseQueryMap);
            Response foundResponse = responseCollection.findOne(jsonResponseQuery).as(Response.class);

            responseId = foundResponse.getId();

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

          Task task = taskCollection.findOne(Oid.withOid(funcResponse.recordId)).as(Task.class);

           task.toReceivedTime = receivedTime;
           task.toResponseBodyId = responseBodyId;
           task.fromSentTime = fromSentTime;

          taskCollection
                  .update(new ObjectId(task.getId()))
                  .with(new Task(task));

      } catch(Exception e) {
          e.printStackTrace();
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
            String task = funcTask.task.toString();
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
            String newInitTask = new Gson().toJson(newInitTaskRecord);
            tasksCollection.insert(newInitTask);

            Map<String, String> taskQueryMap = new HashMap();
            taskQueryMap.put("requestBodyId", requestBodyId);
            String jsonTaskQuery = new Gson().toJson(taskQueryMap);

            Task newTask = tasksCollection.findOne(jsonTaskQuery).as(Task.class);

            parsedTask = getNewParsedTask(newTask, selectedContainerInfo);

        } catch(Exception e) {
            e.printStackTrace();
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
           responseQueryMap.put("response", funcResponse.responseBody);

           String jsonResponseQuery = new Gson().toJson(responseQueryMap);

           Response preexistingResponse = responseCollection.findOne(jsonResponseQuery).as(Response.class);

           if(preexistingResponse == null) {
              String toReceivedTime = getCurrentISOTimeString();
              String toResponseBodyId = saveNewResponseAndGetID(funcResponse.responseBody);

              completeRecordInDB(funcResponse, toReceivedTime, toResponseBodyId);
           }

           MongoCollection taskCollection = getTaskCollection();

           Task task = taskCollection.findOne(Oid.withOid(funcResponse.recordId)).as(Task.class);

           MongoCursor<Task> cursor = taskCollection.find().as(Task.class);

           if(task == null) {
               for(Task t : cursor) {
                  if(t.getId().equals(funcResponse.recordId)) {
                      task = t;
                      break;
                  }
               }
           }
           cursor.close();

           response = getParsedResponse(funcResponse, task);

       } catch(Exception e) {
           e.printStackTrace();
           Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "completeExistingTaskRecordInDB", e.getMessage());
       } finally {
           return response;
       }
    }
}
