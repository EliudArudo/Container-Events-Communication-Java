package dockerapi;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.messages.Container;
import interfaces.ContainerInfoInterface;
import interfaces.STATUS_TYPE;
import log.Logging;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import static util.Util.getSelectedEventContainerIdAndService;

// https://github.com/spotify/docker-client/blob/master/docs/user_manual.md

// Find out a way to create example containers which we'll test
// method manipulation with

public class ContainerInfo {
    private static String packageName = "dockerapi::ContainerInfo";

    private String id;
    private String service;

    public ContainerInfo() {};

    public ContainerInfo(String id, String service) {
        this.id = id;
        this.service = service;
    };

    public ContainerInfoInterface fetchContainerInfo() {
        try {
            initialise();

            ContainerInfoInterface containerInfo = new ContainerInfoInterface(id, service);
            return containerInfo;
        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "fetchContainerInfo", e.getMessage());
            return null;
        }
    }

    public ContainerInfoInterface fetchOfflineContainerInfo() {
        ContainerInfoInterface containerInfo = new ContainerInfoInterface(id, service);
        return containerInfo;
    }

    public ContainerInfoInterface fetchEventContainer() {
        try {
            ContainerInfoInterface containerInfo = getSelectedEventContainerIdAndService();
            return containerInfo;
        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "fetchEventContainer", e.getMessage());
            return null;
        }
    }

    public ArrayList<ContainerInfoInterface> getFreshContainers() {
            ArrayList<ContainerInfoInterface> parsedContainers = new ArrayList();

        try {
            List<Container> containerArray = getDockerContainerList();

            if(containerArray.size() == 0)
                throw new Error("getFreshContainers: No Containers to parse");

            parsedContainers = getParsedContainers(containerArray);

        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "getFreshContainers", e.getMessage());
        } finally {
            return parsedContainers;
        }

    }

    public void initialise() {
        try {
            List containerArray = getDockerContainerList();
            setContainerInfoUsingContainerArray(containerArray);
        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "initialise", e.getMessage());
        }
    }

    public List<Container> getDockerContainerList() {
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

    public void setContainerInfoUsingContainerArray(List<Container> containerArray) {
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

    public ArrayList<ContainerInfoInterface> getParsedContainers(List<Container> containerArray) throws Exception  {

        try {

            ArrayList<ContainerInfoInterface> parsedContainers = new ArrayList();

            if(containerArray.size() == 0)
                throw new Error("getParsedContainers: No containers brought in");

            for(Container container:containerArray) {
                String id = container.id();
                String service = container.labels().get("com.docker.swarm.service.name");

                ContainerInfoInterface parsedContainerInfo = new ContainerInfoInterface(id, service);

                parsedContainers.add(parsedContainerInfo);
            }

            return parsedContainers;

        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "getParsedContainers", e.getMessage());
            return new ArrayList<>();
        }
    }
}
