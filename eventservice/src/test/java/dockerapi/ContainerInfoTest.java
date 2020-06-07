package dockerapi;

import com.spotify.docker.client.messages.Container;
import interfaces.ContainerInfoInterface;
import mock.MockDockerContainer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ContainerInfoTest {

    @Test
    public void TestFetchOfflineContainerInfo() {
        String dummyContainerID = "dummyContainerID";
        String dummyContainerService = "dummyContainerService";

        DockerAPI dockerAPI = new DockerAPI(dummyContainerID, dummyContainerService);

        ContainerInfoInterface fetchedContainerInfo = dockerAPI.fetchOfflineContainerInfo();

        assertEquals(dummyContainerID, fetchedContainerInfo.id);
        assertEquals(dummyContainerService, fetchedContainerInfo.service);
    }

    @Test
    public void TestGetParsedContainers() {
        DockerAPI dockerAPI = new DockerAPI();

        String dummyContainerID = "dummyContainerID";
        String dummyContainerService = "dummyContainerService";

        Container container = new MockDockerContainer(dummyContainerID, dummyContainerService);

        List<Container> containerArray = new ArrayList<>();
        containerArray.add(container);

        try {
//            ArrayList<ContainerInfoInterface> parsedContainers = dockerAPI.getParsedContainers(containerArray);
//            ContainerInfoInterface parsedContainer = parsedContainers.get(0);
//
//            assertEquals(dummyContainerID, parsedContainer.id);
//            assertEquals(dummyContainerService, parsedContainer.service);

        } catch(Exception e) {
            System.out.println("TestGetParsedContainers: " + e.getMessage());
        }
    }

    @Test
    public void TestSetContainerInfoUsingContainerArray() {
        try {
            DockerAPI dockerAPI = new DockerAPI();

            String dummyContainerID = InetAddress.getLocalHost().getHostName();
            String dummyContainerService = "dummyContainerService";

            Container container = new MockDockerContainer(dummyContainerID, dummyContainerService);

            List<Container> containerArray = new ArrayList<>();
            containerArray.add(container);

            dockerAPI.setContainerInfoUsingContainerArray(containerArray);

            ContainerInfoInterface fetchedOfflineContainerInfo = dockerAPI.fetchOfflineContainerInfo();

            assertEquals(dummyContainerService, fetchedOfflineContainerInfo.service);

        } catch(UnknownHostException e) {
            System.out.println("TestSetContainerInfoUsingContainerArray: " + e.getMessage());
        }
    }

}
