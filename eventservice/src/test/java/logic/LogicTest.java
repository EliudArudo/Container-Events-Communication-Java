package logic;

import interfaces.EventInterface;
import interfaces.InitialisedRecordInfoInterface;
import interfaces.SUB_TASK_TYPE;
import interfaces.TASK_TYPE;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class LogicTest {

    EventInterface dummyTask = new EventInterface();

    @Before
    public void init() {

        dummyTask.requestId = "dummyRequestID";
        dummyTask.containerId = "dummyContainerID";
        dummyTask.service = "dummyService";
        dummyTask.responseBody = "";

    }

    @Test
    public void TestGetParsedResponseInfo() {
        InitialisedRecordInfoInterface dummyExistingRecordInfo = new InitialisedRecordInfoInterface();

        dummyExistingRecordInfo.task = TASK_TYPE.NUMBER;
        dummyExistingRecordInfo.subtask = SUB_TASK_TYPE.ADD;
        dummyExistingRecordInfo.containerId = "";
        dummyExistingRecordInfo.containerService = "";
        dummyExistingRecordInfo.recordId = "";
        dummyExistingRecordInfo.responseBody = "dummyExistingRecordInfo";
        dummyExistingRecordInfo.serviceContainerId = "";
        dummyExistingRecordInfo.serviceContainerService = "";

        EventInterface response = Logic.getParsedResponseInfo(dummyTask, dummyExistingRecordInfo);

        assertEquals(response.recordId, dummyTask.recordId);
        assertEquals(response.containerId, dummyTask.containerId);
        assertEquals(response.service, dummyTask.service);
        assertEquals(response.responseBody, dummyExistingRecordInfo.responseBody);

    }

    @Test
    public void TestParseEventFromRecordInfo() {
        InitialisedRecordInfoInterface dummyInitRecordInfo = new InitialisedRecordInfoInterface();
        dummyInitRecordInfo.chosenContainerId = "dummyChosenContainerId";
        dummyInitRecordInfo.chosenContainerService = "dummyChosenContainerService";
        dummyInitRecordInfo.recordId = "";
        dummyInitRecordInfo.task = TASK_TYPE.NUMBER;
        dummyInitRecordInfo.subtask = SUB_TASK_TYPE.ADD;
        dummyInitRecordInfo.serviceContainerId = "";
        dummyInitRecordInfo.serviceContainerService = "";
        dummyInitRecordInfo.requestBody = "";
        dummyInitRecordInfo.containerId = "";
        dummyInitRecordInfo.containerService = "";

        EventInterface event = Logic.parseEventFromRecordInfo(dummyInitRecordInfo);

        assertEquals(event.containerId, dummyInitRecordInfo.chosenContainerId);
        assertEquals(event.service, dummyInitRecordInfo.chosenContainerService);

    }
}
