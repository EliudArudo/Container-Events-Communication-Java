package Tasks;

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
    private static final int EXPIRATIONTIME = WAITINGTIMEFORRESPONSE * 100 * 5;

    public Task () {}

    public static TASK_TYPE determineTask(String requestBody) {
        TASK_TYPE task;
        boolean isString = false;
        boolean isNumber = false;

        Map<String, Object> mappedRequestBody = new Gson().fromJson(requestBody, Map.class);
        Set<String> mapKeySet = mappedRequestBody.keySet();

        for(String key:mapKeySet) {
            String value = (String) mappedRequestBody.get(key);
            isNumber = value.matches("[+-]?\\d*(\\.\\d+)?");
            isString =  !isNumber;
        }

        task = isString? TASK_TYPE.STRING :
                isNumber? TASK_TYPE.NUMBER :
                        null;

        return task;
    }

    public static SUB_TASK_TYPE determineSubTask(TASK_TYPE task, String requestBody) {
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

    public static TaskInterface taskDeterminer(TASK_TYPE task, SUB_TASK_TYPE subtask,String requestBody, ContainerInfo containerInfo) {
           try {
               if(task.equals(null) || subtask.equals(null))
                   throw new Exception("Task not properly categorised");

               ContainerInfoInterface myContainerInfo = containerInfo.fetchContainerInfo();

               String requestId = UUID.randomUUID().toString();

               ContainerInfoInterface chosenContainer = containerInfo.fetchEventContainer();

               String newRequestBody = requestBody.replaceAll("[\\n\\t ]", "");

               TaskInterface exportTask = new TaskInterface(
                       task,
                       subtask,
                       myContainerInfo.id,
                       myContainerInfo.service,
                       requestId,
                       newRequestBody,
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

            String EventService = EnvSetup.EventServiceEvent;
            functionRedisPublisher.publish(EventService, stringifiedTask);

        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "sendTaskToEventsService", e.getMessage());
        }
    }

    public static Object waitForResult(String requestId) {
          try {
              ReceivedEventInterface response = Util.getResponseFromBuffer(requestId);

              int waitingTimeCounter = 0;

              while(response.responseBody == null) {
                  if(waitingTimeCounter >= EXPIRATIONTIME)
                       throw new Exception("Response took too long");

                  Thread.sleep(WAITINGTIMEFORRESPONSE);
                  response = Util.getResponseFromBuffer(requestId);

                  waitingTimeCounter+= WAITINGTIMEFORRESPONSE;
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
