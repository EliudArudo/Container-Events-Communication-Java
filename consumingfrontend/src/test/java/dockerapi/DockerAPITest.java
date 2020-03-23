package dockerapi;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.messages.Container;
import interfaces.ContainerInfoInterface;
import mock.MockDockerContainer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class DockerAPITest {

    @Test
    public void TestFetchOfflineContainerInfo() {
        String dummyContainerID = "dummyContainerID";
        String dummyContainerService = "dummyContainerService";

        ContainerInfo  containerInfo = new ContainerInfo(dummyContainerID, dummyContainerService);

        ContainerInfoInterface fetchedContainerInfo = containerInfo.fetchOfflineContainerInfo();

        assertEquals(dummyContainerID, fetchedContainerInfo.id);
        assertEquals(dummyContainerService, fetchedContainerInfo.service);
    }

    @Test
    public void TestGetParsedContainers() {
        ContainerInfo  containerInfo = new ContainerInfo();

        String dummyContainerID = "dummyContainerID";
        String dummyContainerService = "dummyContainerService";

        Container container = new MockDockerContainer(dummyContainerID, dummyContainerService);

        List<Container> containerArray = new ArrayList<>();
        containerArray.add(container);

        try {
            ArrayList<ContainerInfoInterface> parsedContainers = containerInfo.getParsedContainers(containerArray);
            ContainerInfoInterface parsedContainer = parsedContainers.get(0);

            assertEquals(dummyContainerID, parsedContainer.id);
            assertEquals(dummyContainerService, parsedContainer.service);

        } catch(Exception e) {
            System.out.println("TestGetParsedContainers: " + e.getMessage());
        }
    }

    @Test
    public void TestSetContainerInfoUsingContainerArray() {
        try {
            ContainerInfo  containerInfo = new ContainerInfo();

            String dummyContainerID = InetAddress.getLocalHost().getHostName();
            String dummyContainerService = "dummyContainerService";

            Container container = new MockDockerContainer(dummyContainerID, dummyContainerService);

            List<Container> containerArray = new ArrayList<>();
            containerArray.add(container);

            containerInfo.setContainerInfoUsingContainerArray(containerArray);

            ContainerInfoInterface fetchedOfflineContainerInfo = containerInfo.fetchOfflineContainerInfo();

            assertEquals(dummyContainerService, fetchedOfflineContainerInfo.service);

        } catch(UnknownHostException e) {
            System.out.println("TestSetContainerInfoUsingContainerArray: " + e.getMessage());
        }
    }

    @Test
    public void TestGetSelectedEventContainerIdAndService() {
           // TODO - Continue here
            DockerClient mockDocker = Mockito.mock(DefaultDockerClient.class);

            ContainerInfo  containerInfo = Mockito.mock(ContainerInfo.class);

            String dummyEventContainerID = "dummyEventContainerID";
            String dummyEventContainerService = "event";

            Container dummyEventContainer = new MockDockerContainer(dummyEventContainerID, dummyEventContainerService);

            List<Container> containerArray = new ArrayList<>();
            containerArray.add(dummyEventContainer);

            try {
                Mockito.when(mockDocker.listContainers()).thenReturn(containerArray);

                Mockito.when(containerInfo.getDockerContainerList()).thenReturn(containerArray);

                ContainerInfoInterface selectedEventContainer = containerInfo.fetchEventContainer();

                System.out.println("selectedEventContainer.id: " + selectedEventContainer.id);
                System.out.println("selectedEventContainer.service: " + selectedEventContainer.service);

            } catch(Exception e) {
                e.printStackTrace();
            }
    }
}
