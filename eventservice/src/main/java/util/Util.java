package util;

import dockerapi.DockerAPI;
import interfaces.ContainerInfoInterface;
import interfaces.EventInterface;
import interfaces.ParsedContainerInterface;
import interfaces.STATUS_TYPE;
import log.Logging;

import java.util.ArrayList;
import java.util.List;

public class Util {
    private static String packageName = "Util::Util";

    public static ContainerInfoInterface getSelectedContainerIdAndService(EventInterface task) {
       ContainerInfoInterface selectedContainer = null;

       try {
           List<ParsedContainerInterface> containers = DockerAPI.getFreshContainers();

           List<ParsedContainerInterface> selectedContainers = new ArrayList<>();

           // TODO - Continue from here 2




       } catch(Exception e) {
           Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "getSelectedContainerIdAndService", e.getMessage());
       } finally {
           return  selectedContainer;
       }
    }
}
