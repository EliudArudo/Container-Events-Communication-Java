package dockerapi;

import interfaces.ContainerInfoInterface;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

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
    public void TestSetContainerINfoUsingContainerArray() {
        // Continue from here
    }
}
