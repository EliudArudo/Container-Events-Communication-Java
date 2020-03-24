package logic;

import com.google.gson.Gson;
import dockerapi.ContainerInfo;
import interfaces.ContainerInfoInterface;
import interfaces.ReceivedEventInterface;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import util.Util;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class LogicTest {

    private ReceivedEventInterface event = new ReceivedEventInterface();
    private ContainerInfo dummyContainerInfoClass = Mockito.mock(ContainerInfo.class);

    private String dummyContainerID = "dummyContainerID";
    private String dummyContainerService = "dummyContainerService";
    private String dummyRequestID = "dummyRequestID";
    private String dummyResponseBody = "dummyResponse";

    private ContainerInfoInterface dummyContainerInfo = new ContainerInfoInterface(dummyContainerID, dummyContainerService);
    private ReceivedEventInterface recievedEvent = new ReceivedEventInterface();

    @Before
    public void init() {
        Mockito.when(dummyContainerInfoClass.fetchOfflineContainerInfo()).thenReturn(dummyContainerInfo);
        event.requestId = dummyRequestID;
        event.containerId = dummyContainerID;
        event.service = dummyContainerService;
        event.responseBody = dummyResponseBody;
    }

    @Test
    public void TestEventPushedToBuffers() {
        String stringifiedDummyEvent = new Gson().toJson(event);
        Logic.eventDeterminer(stringifiedDummyEvent, dummyContainerInfoClass);

        ReceivedEventInterface recievedEvent = Util.getResponseFromBuffer(event.requestId);

        assertEquals(dummyResponseBody, recievedEvent.responseBody);
        assertEquals(dummyRequestID, recievedEvent.requestId);
    }

    @Test
    public void TestEventClearedFromBuffers() {
        String stringifiedDummyEvent = new Gson().toJson(event);
        Logic.eventDeterminer(stringifiedDummyEvent, dummyContainerInfoClass);

        ReceivedEventInterface _ = Util.getResponseFromBuffer(event.requestId);
        ReceivedEventInterface nullEvent = Util.getResponseFromBuffer(event.requestId);

        assertEquals(null, nullEvent.requestId);
    }
}
