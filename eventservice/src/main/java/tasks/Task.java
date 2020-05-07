package tasks;

import com.google.gson.Gson;
import dockerapi.DockerAPI;
import interfaces.*;
import log.Logging;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Task {
    private static String packageName = "tasks::Task";

    private static final int WAITINGTIMEFORRESPONSE = 5;
    private static final int EXPIRATIONTIME = WAITINGTIMEFORRESPONSE * 1000;

    public Task () {}

    public static TASK_TYPE determineTask(String requestBody) {
        TASK_TYPE task;
        boolean isString = false;
        boolean isNumber = false;

        Map<String, Object> mappedRequestBody = new Gson().fromJson(requestBody, Map.class);
        Set<String> mapKeySet = mappedRequestBody.keySet();

        for(Object key:mapKeySet) {

            Object rawValue = mappedRequestBody.get(key);

            // String value = (String) mappedRequestBody.get(key);
            String value = rawValue.toString();

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

    public static TaskInterface taskDeterminer(TASK_TYPE task, SUB_TASK_TYPE subtask,String requestBody, DockerAPI dockerAPI) {
           try {
               if(task.equals(null) || subtask.equals(null))
                   throw new Exception("Task not properly categorised");

               ContainerInfoInterface myContainerInfo = dockerAPI.fetchContainerInfo();

               String requestId = UUID.randomUUID().toString();

               ContainerInfoInterface chosenContainer = dockerAPI.fetchContainerInfo();

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

//    public static void sendTaskToEventsService(TaskInterface task, Jedis functionRedisPublisher) {
//        try {
//            String stringifiedTask = new Gson().toJson(task);
//
//            String EventService = EnvSetup.EventServiceEvent;
//            functionRedisPublisher.publish(EventService, stringifiedTask);
//
//        } catch(Exception e) {
//            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "sendTaskToEventsService", e.getMessage());
//        }
//    }

}
