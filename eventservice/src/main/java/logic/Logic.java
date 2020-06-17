package logic;

import com.google.gson.Gson;
import databaseops.MongoDBOps;
import dockerapi.DockerAPI;
import env.EnvSetup;
import initialize.RedisInit;
import interfaces.*;
import log.Logging;
import redis.clients.jedis.Jedis;

public class Logic {
    private static String packageName = "logic::Logic";

    public static void eventDeterminer(String sentEvent, DockerAPI functionDockerAPI) {

        try {
//            System.out.println("\n sentEvent :\n " + sentEvent + "\n");
            EventInterface event = new Gson().fromJson(sentEvent, EventInterface.class);

            ContainerInfoInterface offlineContainerInfo = functionDockerAPI.fetchOfflineContainerInfo();

            boolean eventIsOurs = event.serviceContainerId.equals(offlineContainerInfo.id) && event.serviceContainerService.equals(offlineContainerInfo.service);

            EventTaskType taskType = (event.responseBody != null && event.responseBody.length() > 0)? EventTaskType.RESPONSE :
                    EventTaskType.TASK;

            if(!eventIsOurs)
                return;

            switch (taskType) {
                case TASK:
                    System.out.println("\nGot task event\n");
                    recordAndAllocateTask(event);
                    break;
                case RESPONSE:
                    System.out.println("\nGot response event\n");
                    modifyDatabaseAndSendBackResponse(event);
            }

        } catch(Exception e) {
            e.printStackTrace();
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "eventDeterminer", e.getMessage());
        }
    }

    public static EventInterface getParsedResponseInfo(EventInterface task, InitialisedRecordInfoInterface existingRecordInfo) {
        EventInterface parsedResponseInfo = new EventInterface();

        parsedResponseInfo.requestId = task.requestId;
        parsedResponseInfo.containerId = task.containerId;
        parsedResponseInfo.service = task.service;
        parsedResponseInfo.responseBody = existingRecordInfo.responseBody;

        return parsedResponseInfo;
    }

    public static void recordAndAllocateTask(EventInterface task) {
        InitialisedRecordInfoInterface initRecordInfo = MongoDBOps.recordNewTaskInDB(task);

        if(initRecordInfo != null && initRecordInfo.existing != null && initRecordInfo.existing) {
            EventInterface responseInfo = getParsedResponseInfo(task, initRecordInfo);
            sendEventToContainer(responseInfo);
            return;
        }

        allocateTaskToConsumingContainer(initRecordInfo);
    }

    public static void modifyDatabaseAndSendBackResponse(EventInterface response) {
        try {

            EventInterface responseInfo = MongoDBOps.completeExistingTaskRecordInDB(response);
            sendEventToContainer(responseInfo);

        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "modifyDatabaseAndSendBackResponse", e.getMessage());
        }
    }

    public static void sendEventToContainer(EventInterface  eventInfo) {
         String stringifiedResponse = new Gson().toJson(eventInfo);

         Jedis redisPublisher = RedisInit.getRedisPublisher();
         String ConsumingService = EnvSetup.ConsumingServiceEvent;

         redisPublisher.publish(ConsumingService, stringifiedResponse);
    }

    public  static EventInterface parseEventFromRecordInfo(InitialisedRecordInfoInterface initRecordInfo) {
        EventInterface event = new EventInterface();

        event.containerId = initRecordInfo.chosenContainerId;
        event.service = initRecordInfo.chosenContainerService;
        event.recordId = initRecordInfo.recordId;
        event.task = initRecordInfo.task;
        event.subtask = initRecordInfo.subtask;
        event.serviceContainerId = initRecordInfo.serviceContainerId;
        event.serviceContainerService = initRecordInfo.serviceContainerService;
        event.requestBody = initRecordInfo.requestBody;

        return event;
    }

    public static void allocateTaskToConsumingContainer(InitialisedRecordInfoInterface initRecordInfo) {
       EventInterface eventToSend = parseEventFromRecordInfo(initRecordInfo);
       sendEventToContainer(eventToSend);
    }

}
