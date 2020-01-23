package tasks;

import com.google.gson.Gson;
import dockerapi.ContainerInfo;
import env.EnvSetup;
import initialize.RedisInit;
import interfaces.*;
import log.Logging;
import redis.clients.jedis.Jedis;
import util.Util;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Task {
    private static String packageName = "tasks::Task";

    private static final int WAITINGTIMEFORRESPONSE = 10;

    public Task () {}

    private static TASK_TYPE determineTask(String requestBody) {
        TASK_TYPE task;
        boolean isString = false;
        boolean isNumber = false;

        System.out.println("------> requestBody is: " + requestBody);

        Map<String, Object> mappedRequestBody = new Gson().fromJson(requestBody, Map.class);
        Set<String> mapKeySet = mappedRequestBody.keySet();

        for(String key:mapKeySet) {
           isString = mappedRequestBody.get(key) instanceof String;
           isNumber = mappedRequestBody.get(key) instanceof Number;
        }

        task = isString? TASK_TYPE.NUMBER :
                isNumber? TASK_TYPE.STRING :
                        null;

        return task;
    }

    private static SUB_TASK_TYPE determineSubTask(TASK_TYPE task, String requestBody) {
        SUB_TASK_TYPE subtask = null;

        switch(task) {
            case STRING:
                subtask = SUB_TASK_TYPE.ADD;
                break;
            case NUMBER:
                boolean isAddition = false;
                boolean isSubtraction = false;
                boolean isMultiplication = false;
                boolean isDivision = false;

                Map<String, Object> mappedRequestBody = new Gson().fromJson(requestBody, Map.class);;
                Set<String> mapKeySet = mappedRequestBody.keySet();

                for(String key:mapKeySet) {
                    isAddition = key.contains("a");
                    isSubtraction = key.contains("s");
                    isMultiplication = key.contains("m");
                    isDivision = key.contains("d");
                }

                subtask = isAddition? SUB_TASK_TYPE.ADD :
                        isSubtraction? SUB_TASK_TYPE.SUBTRACT :
                                isMultiplication? SUB_TASK_TYPE.MULTIPLY:
                                        isDivision? SUB_TASK_TYPE.DIVIDE : null;
                break;
        }

        return subtask;
    }

    private static TaskInterface taskDeterminer(TASK_TYPE task, SUB_TASK_TYPE subtask,Object requestBody, ContainerInfo containerInfo) {
           try {
               if(task.equals(null) || subtask.equals(null))
                   throw new Exception("Task not properly categorised");

               ContainerInfoInterface myContainerInfo = containerInfo.fetchContainerInfo();

               String requestId = UUID.randomUUID().toString();

               ContainerInfoInterface chosenContainer = containerInfo.fetchEventContainer();

               String stringifiedRequestBody = new Gson().toJson(requestBody);

               TaskInterface exportTask = new TaskInterface(
                       task,
                       subtask,
                       myContainerInfo.id,
                       myContainerInfo.service,
                       requestId,
                       stringifiedRequestBody,
                       chosenContainer.id,
                       chosenContainer.service
               );

               return exportTask;
           } catch(Exception e) {
               Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "taskDeterminer", e.getMessage());
               return null;
           }
    }

    public static void sendTaskToEventsService(TaskInterface task, Jedis functionRedisPublisher) {
        try {
            String stringifiedTask = new Gson().toJson(task);
            // Dev
            System.out.println("------> About to be sent through redis" + stringifiedTask);
            // Dev


            String EventService = EnvSetup.EventServiceEvent;
            functionRedisPublisher.publish(EventService, stringifiedTask);

        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "sendTaskToEventsService", e.getMessage());
        }
    }

    private static Object waitForResult(String requestId) {
          try {
              ReceivedEventInterface response = Util.getResponseFromBuffer(requestId);

              while(response.equals(null) || response.responseBody.length() == 0) {
                  Thread.sleep(WAITINGTIMEFORRESPONSE);
                  response = Util.getResponseFromBuffer(requestId);
              }

              return response.responseBody;

          } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "waitForResult", e.getMessage());
            return null;
          }
    }

    public static Object taskController(String requestBody, ContainerInfo containerInfo) {
        try {

            TASK_TYPE determinedTask  = determineTask(requestBody);
            SUB_TASK_TYPE determinedSubtask = determineSubTask(determinedTask, requestBody);
            TaskInterface task = taskDeterminer(determinedTask, determinedSubtask, requestBody, containerInfo);

            Jedis redisPublisher = RedisInit.getRedisPublisher();

            sendTaskToEventsService(task, redisPublisher);

            Object response = waitForResult(task.requestId);

            return response;
        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "taskController", e.getMessage());
            return null;
        }
    }
}
