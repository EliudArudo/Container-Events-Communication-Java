package dockerapi;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.messages.Container;
import interfaces.ContainerInfoInterface;
import interfaces.ParsedContainerInterface;
import interfaces.STATUS_TYPE;
import log.Logging;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

// https://github.com/spotify/docker-client/blob/master/docs/user_manual.md

// Find out a way to create example containers which we'll test
// method manipulation with

public class DockerAPI {
    private static String packageName = "dockerapi::ContainerInfo";

    private static String id;
    private static String service;

    public DockerAPI() {};

    public DockerAPI(String id, String service) {
        this.id = id;
        this.service = service;
    };

    public static ContainerInfoInterface fetchContainerInfo() {
        try {
            initialise();

            ContainerInfoInterface containerInfo = new ContainerInfoInterface(id, service);
            return containerInfo;
        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "fetchContainerInfo", e.getMessage());
            return null;
        }
    }

    public static ContainerInfoInterface fetchOfflineContainerInfo() {
        ContainerInfoInterface containerInfo = new ContainerInfoInterface(id, service);
        return containerInfo;
    }


    public static void initialise() {
        try {
            List containerArray = getDockerContainerList();
            setContainerInfoUsingContainerArray(containerArray);
        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "initialise", e.getMessage());
        }
    }

    public static List<Container> getDockerContainerList() {
             List<Container> containerArray;

         try {
             final DockerClient docker = DefaultDockerClient.fromEnv().build();

             containerArray = docker.listContainers();

             return containerArray;

         } catch(Exception e) {
             Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "getDockerContainerList", e.getMessage());
             return new ArrayList<>();
         }
    }

    public static List<ParsedContainerInterface> getFreshContainers() {
        try {

            List<Container> containerArray = getDockerContainerList();

            if(containerArray.size() == 0) {
                throw new Exception("getFreshContainers: No containers to parse");
            }

            List<ParsedContainerInterface> parsedContainers = getParsedContainers(containerArray);

            return parsedContainers;
        } catch(Exception e) {
             Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "getFreshContainers", e.getMessage());
             return new ArrayList<>();
        }
    }

    public static void setContainerInfoUsingContainerArray(List<Container> containerArray) {
        try {
          String shortContainerId = InetAddress.getLocalHost().getHostName();

          if(containerArray.size() == 0)
              throw new Error("No containers available");

          Container fetchedContainer = containerArray
                  .stream()
                  .filter(container -> container.id().contains(shortContainerId))
                  .findAny()
                  .orElse(null);

          id = fetchedContainer.id();
          service = fetchedContainer.labels().get("com.docker.swarm.service.name");

        } catch (Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "setContainerInfoUsingContainerArray", e.getMessage());
        }
    }

    public static List<ParsedContainerInterface> getParsedContainers(List<Container> containerArray) throws Exception  {

        try {

            ArrayList<ParsedContainerInterface> parsedContainers = new ArrayList();

            if(containerArray.size() == 0)
                throw new Error("getParsedContainers: No containers brought in");

            for(Container container:containerArray) {
                String containerId = container.id();
                String containerService = container.labels().get("com.docker.swarm.service.name");

                ParsedContainerInterface parsedContainerInfo = new ParsedContainerInterface(containerId, containerService);

                parsedContainers.add(parsedContainerInfo);
            }

            return parsedContainers;

        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "getParsedContainers", e.getMessage());
            return new ArrayList<>();
        }
    }
}
