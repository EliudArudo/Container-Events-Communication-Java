package dockerapi;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.messages.Container;
import interfaces.ContainerInfoInterface;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static util.Util.getSelectedEventContainerIdAndService;

// https://github.com/spotify/docker-client/blob/master/docs/user_manual.md

public class ContainerInfo {
    private String id;
    private String service;

    public ContainerInfo() {};

    public ContainerInfoInterface fetchContainerInfo() {
        initialise();
        ContainerInfoInterface containerInfo = new ContainerInfoInterface(id, service);
        return containerInfo;
    }

    public ContainerInfoInterface fetchOfflineContainerInfo() {
        ContainerInfoInterface containerInfo = new ContainerInfoInterface(id, service);
        return containerInfo;
    }

    public ContainerInfoInterface fetchEventContainer() {
        ContainerInfoInterface containerInfo = getSelectedEventContainerIdAndService();

        return containerInfo;
    }

    public ArrayList<ContainerInfoInterface> getFreshContainers() {
            ArrayList<ContainerInfoInterface> parsedContainers = new ArrayList();

        try {
            List<Container> containerArray = getDockerContainerList();

            if(containerArray.size() == 0)
                throw new Error("getFreshContainers: No Containers to parse");

            parsedContainers = getParsedContainers(containerArray);

        } catch(Exception e) {
            System.out.printf("failed to fetch fresh container arrays %s", e.getMessage());
        } finally {
            return parsedContainers;
        }

    }

    public void initialise() {
        List containerArray = getDockerContainerList();
        setContainerInfoUsingContainerArray(containerArray);
    }

    public List<Container> getDockerContainerList() {
             List<Container> containerArray;

         try {
             final DockerClient docker = DefaultDockerClient.fromEnv().build();

             containerArray = docker.listContainers();

             return containerArray;

         } catch(DockerCertificateException e) {
             System.out.printf("Could not get containers %s", e.getMessage());
         } finally {
             return new ArrayList<Container>();
         }
    }

    public void setContainerInfoUsingContainerArray(List<Container> containerArray) {
        try {
          String shortContainerId = InetAddress.getLocalHost().getHostName();

          if(containerArray.size() == 0)
              throw new Error("No containers available");

          Container fetchedContainer = containerArray
                  .stream()
                  .filter(container -> container.id().equals(shortContainerId))
                  .findAny()
                  .orElse(null);

          id = fetchedContainer.id();
          service = fetchedContainer.labels().get("com.docker.swarm.service.name");

          return;

        } catch (UnknownHostException e) {
            System.out.printf("Could not get hostname %s", e.getMessage());
        } catch (Exception e) {
            System.out.printf("Error: %s", e.getMessage());
        }
    }

    public ArrayList<ContainerInfoInterface> getParsedContainers(List<Container> containerArray) throws Exception  {
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
    }
}
