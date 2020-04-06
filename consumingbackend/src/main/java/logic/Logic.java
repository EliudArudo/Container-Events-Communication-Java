package logic;

import com.google.gson.Gson;
import dockerapi.ContainerInfo;
import env.EnvSetup;
import initialize.RedisInit;
import interfaces.*;
import log.Logging;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Logic {
    private static String packageName = "logic::Logic";

    public static void eventDeterminer(String sentEvent, ContainerInfo functionContainerInfo) {

        try {
            ReceivedEventInterface event = new Gson().fromJson(sentEvent, ReceivedEventInterface.class);

            ContainerInfoInterface offlineContainerInfo = functionContainerInfo.fetchOfflineContainerInfo();

            boolean eventIsOurs = event.containerId.equals(offlineContainerInfo.id) && event.service.equals(offlineContainerInfo.service);

            EventTaskType taskType = event.requestBody.length() > 0? EventTaskType.TASK :
                    EventTaskType.RESPONSE;

            if(!eventIsOurs)
                return;


            switch (taskType) {
                case TASK:
                    performTaskAndRespond(event);
                    break;
                case RESPONSE:
                    // Backend not meant to receive any tasks

            }

        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "eventDeterminer", e.getMessage());
        }
    }

    public static String performLogic(ReceivedEventInterface task) {
        String result = null;

        String data = task.requestBody;

        Map<String, Object> mappedRequestBody = new Gson().fromJson(data, Map.class);
        Set<String> mapKeySet = mappedRequestBody.keySet();

        List<String> mapKeySetArray = new ArrayList();

        for(String key: mapKeySet) {
            mapKeySetArray.add(key);
        }

        String keyForItem1 = mapKeySetArray.get(0);
        String keyForItem2 = mapKeySetArray.get(1);

        String item1 = mappedRequestBody.get(keyForItem1).toString();
        String item2 = mappedRequestBody.get(keyForItem2).toString();

        if(task.task == TASK_TYPE.STRING && task.subtask == SUB_TASK_TYPE.ADD) {
            result = devAddStrings(item1, item2);
        } else {
            result = task.subtask == SUB_TASK_TYPE.ADD?
                  devAddNumber(item1, item2) :
                  task.subtask == SUB_TASK_TYPE.SUBTRACT?
                  devSubtractNumber(item1, item2) :
                  task.subtask == SUB_TASK_TYPE.MULTIPLY?
                  devMultiplyNumber(item1, item2) :
                  task.subtask == SUB_TASK_TYPE.DIVIDE?
                  devDivideNumber(item1, item2): null;
        }

        return result;
    }

    public static void sendResultsToEventService(ReceivedEventInterface task, String results) {
        try {

            ReceivedEventInterface event = new ReceivedEventInterface();
            event.containerId = task.containerId;
            event.service = task.service;
            event.recordId = task.recordId;
            event.serviceContainerId = task.serviceContainerId;
            event.serviceContainerService = task.serviceContainerService;
            event.responseBody = results;

            String stringifiedEvents = new Gson().toJson(event);

            String EventService = EnvSetup.EventServiceEvent;
            Jedis redisPublisher = RedisInit.getRedisPublisher();

            redisPublisher.publish(EventService, stringifiedEvents);

        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "sendResultsToEventService", e.getMessage());
        }
    }

    public static void performTaskAndRespond(ReceivedEventInterface task) {
        String results = performLogic(task);
        sendResultsToEventService(task, results);
    }

    public static String devAddStrings(String string1, String string2) {
       String concatString = string1 + string2;
        return concatString;
    }


    public static String devAddNumber(String number1, String number2) {
        Double addedNumber = Double.parseDouble(number1) + Double.parseDouble(number2);

        return addedNumber.toString();
    }


    public static String devSubtractNumber(String number1, String number2) {
        Double subtractedNumber = Double.parseDouble(number1) - Double.parseDouble(number2);

        return subtractedNumber.toString();
    }


    public static String devMultiplyNumber(String number1, String number2) {
        Double multipliedNumber = Double.parseDouble(number1) * Double.parseDouble(number2);

        return multipliedNumber.toString();
    }


    public static String devDivideNumber(String number1, String number2) {
        Double dividedNumber = Double.parseDouble(number1) / Double.parseDouble(number2);

        return dividedNumber.toString();
    }
}
