package tasks;

import com.google.gson.Gson;
import dockerapi.ContainerInfo;
import interfaces.*;
import util.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Task {

    private static final int WAITINGTIMEFORRESPONSE = 10;

    public Task () {}

    private static TASK_TYPE determineTask(Object requestBody) {
        TASK_TYPE task;
        boolean isString = false;
        boolean isNumber = false;

        Map<String, Object> mappedRequestBody = (HashMap<String, Object>) requestBody;
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

    private static SUB_TASK_TYPE determineSubTask(TASK_TYPE task, Object requestBody) {
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

                Map<String, Object> mappedRequestBody = (HashMap<String, Object>) requestBody;
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
              // Will do something about this
           }

           return null;
    }

    public static void sendTaskToEventsService(TaskInterface task, Object functionRedisPublisher) {
        String stringifiedTask = new Gson().toJson(task);
//        functionRedisPublisher.publish(EventService, stringifiedTask);
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

          }

          return null;
    }

    public static Object taskController(Object requestBody, ContainerInfo containerInfo) {
        try {
            TASK_TYPE determinedTask  = determineTask(requestBody);
            SUB_TASK_TYPE determinedSubtask = determineSubTask(determinedTask, requestBody);
            TaskInterface task = taskDeterminer(determinedTask, determinedSubtask, requestBody, containerInfo);

            Object redisPublisher = null;
            sendTaskToEventsService(task, redisPublisher);

            Object response = waitForResult(task.requestId);

            return response;

        } catch(Exception e) {

        }
        return null;
    }
}
