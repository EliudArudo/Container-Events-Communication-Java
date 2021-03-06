package logic;

import com.google.gson.Gson;
import dockerapi.ContainerInfo;
import interfaces.ContainerInfoInterface;
import interfaces.EventTaskType;
import interfaces.ReceivedEventInterface;
import interfaces.STATUS_TYPE;
import log.Logging;
import util.Util;

public class Logic {
    private static String packageName = "logic::Logic";

    public static void eventDeterminer(String sentEvent, ContainerInfo functionContainerInfo) {

        try {
            if(sentEvent.charAt(0) == '"') {
                sentEvent = sentEvent
                        .substring(1, sentEvent.length() - 1)
                        .replaceAll("\\\\\"", "\"")
                        .replaceAll("\\\\\"", "\"");
            }

            ReceivedEventInterface event = new Gson().fromJson(sentEvent, ReceivedEventInterface.class);

            ContainerInfoInterface offlineContainerInfo = functionContainerInfo.fetchOfflineContainerInfo();
            boolean eventIsOurs = event.containerId.equals(offlineContainerInfo.id) && event.service.equals(offlineContainerInfo.service);

            EventTaskType taskType = event.responseBody.length() > 0? EventTaskType.RESPONSE :
                    EventTaskType.TASK;

            if(!eventIsOurs)
                return;
        /*
          This makes frontend service containers *taskable
          -- taskable - means that they can receive tasks, perform tasks and send back to
            event services
       */

            switch (taskType) {
                case TASK:
                    // Frontend not meant to receive any tasks
                    break;
                case RESPONSE:
               /*
                  Event pushed to response buffers is waited on using 'requestId' as identifier
                  - Use this function to wait for result, http and even Web Sockets
                  as in tasks/index.ts -> TaskController function

                  const response = await waitForResult(task.requestId)
              */
                    Util.pushResponseToBuffers(event);
            }

        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "eventDeterminer", e.getMessage());
        }
    }
}
