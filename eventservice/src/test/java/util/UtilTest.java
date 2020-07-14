package util;

import interfaces.*;
import mock.MockDockerContainer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class UtilTest {

    List<MockDockerContainer> dummyRawDockerContainers = new ArrayList();
    List<ParsedContainerInterface> dummyParsedContainers = new ArrayList();

    @Before
    public void init() {
       MockDockerContainer dummyRawDockerContainer1 = new MockDockerContainer("containerId1", "eventservice");
       MockDockerContainer dummyRawDockerContainer2 = new MockDockerContainer("containerId2", "consumingbackendservice");

       dummyRawDockerContainers.add(dummyRawDockerContainer1);
       dummyRawDockerContainers.add(dummyRawDockerContainer2);

       ParsedContainerInterface dummyParsedContainer1 = new ParsedContainerInterface("containerId1", "eventservice");
       ParsedContainerInterface dummyParsedContainer2 = new ParsedContainerInterface("containerId2", "consumingbackendservice");

       dummyParsedContainers.add(dummyParsedContainer1);
       dummyParsedContainers.add(dummyParsedContainer2);

       try {
             //  Mocking the docker is still problematic

             //  Mockito.when(DefaultDockerClient.fromEnv().build()).thenReturn(null);
             //  Mockito.when(DockerAPI.getFreshContainers()).thenReturn(dummyParsedContainers);

       } catch (Exception e){
           //
        }

    }

    @Test
    public void TestGetSelectedEventContainerIdAndService() {
        EventInterface task = new EventInterface();
        task.task = TASK_TYPE.NUMBER;
        task.subtask = SUB_TASK_TYPE.ADD;
        task.containerId = "";
        task.service = "consumingbackendservice";
        task.requestBody = "";

        ContainerInfoInterface fetchedContainer = Util.getSelectedContainerIdAndService(task);

        //  assertEquals(fetchedContainer.service, dummyParsedContainers.get(1).containerService);
    }

}
