package util;

import dockerapi.DockerAPI;
import interfaces.*;
import log.Logging;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Util {
    private static String packageName = "Util::Util";

    public static ContainerInfoInterface getSelectedContainerIdAndService(EventInterface task) {
       ContainerInfoInterface selectedContainer = null;

       try {
           List<ParsedContainerInterface> containers = DockerAPI.getFreshContainers();

           List<ParsedContainerInterface> selectedContainers = new ArrayList<>();

           JSONParser parser = new JSONParser();
           JSONObject taskMaps = (JSONObject) parser.parse(new FileReader("/app/task-maps.json"));

           String stringifiedTask =
                   task.task == TASK_TYPE.NUMBER? "NUMBER" :
                   task.task == TASK_TYPE.STRING? "STRING" : null;

           String selectedService = (String) taskMaps.get(stringifiedTask);

           for(ParsedContainerInterface container : containers) {
               String lowerCaseContainerService = container.containerService.toLowerCase();
               Boolean containerBelongsToSelectedService =
                  lowerCaseContainerService.contains(selectedService);

               if (containerBelongsToSelectedService) {
                   selectedContainers.add(container);
               }

           }

           int randomIndex = (int) Math.floor(Math.random() * selectedContainers.size());
           ParsedContainerInterface randomlySelectedContainer = selectedContainers.get(randomIndex);

           while(randomlySelectedContainer.containerID == null) {
               getSelectedContainerIdAndService(task);
           }

           String id = randomlySelectedContainer.containerID;
           String service = randomlySelectedContainer.containerService;

           selectedContainer = new ContainerInfoInterface(id, service);

       } catch(Exception e) {
           e.printStackTrace();
           Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "getSelectedContainerIdAndService", e.getMessage());
       } finally {
           return  selectedContainer;
       }
    }
}
