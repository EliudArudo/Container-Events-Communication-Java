package logic;

import com.google.gson.Gson;
import dockerapi.ContainerInfo;
import interfaces.ContainerInfoInterface;
import interfaces.EventTaskType;
import interfaces.ReceivedEventInterface;
import util.Util;

public class Logic {

    public static void eventDeterminer(String sentEvent, ContainerInfo functionContainerInfo) {
        ReceivedEventInterface event = new Gson().fromJson(sentEvent, ReceivedEventInterface.class);

        ContainerInfoInterface offlineContainerInfo = functionContainerInfo.fetchContainerInfo();
        boolean eventIsOurs = event.containerId == offlineContainerInfo.id &&
                event.service == offlineContainerInfo.service;

        EventTaskType taskType = event.responseBody.length() > 0? EventTaskType.RESPONSE :
                EventTaskType.RESPONSE;

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
    }
}
