package util;

import interfaces.ReceivedEventInterface;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class UtilTest {
    private ReceivedEventInterface response = new ReceivedEventInterface();

    private String dummyRequestID = "dummyRequestID";
    private String dummyContainerID = "dummyContainerID";
    private String dummyContainerService = "dummyContainerService";

    @Before
    public void init() {
        response.requestId = dummyRequestID;
        response.containerId = dummyContainerID;
        response.service = dummyContainerService;

        Util.pushResponseToBuffers(response);
    }

    @After
    public void clearUp() {
        Util.getResponseFromBuffer(dummyRequestID);
    }

    // pushResponseToBuffers
    @Test
    public void TestPushResponseToBuffers(){
        ArrayList<String> responseBuffer = Util.getResponseBuffer();
        ArrayList<ReceivedEventInterface> responses = Util.getResponses();

        assertEquals(1, responseBuffer.size());
        assertEquals(1, responses.size());
    }

    // getResponseFromBuffer
    @Test
    public void TestGetResponseFromBuffer() {
        ReceivedEventInterface response = Util.getResponseFromBuffer(dummyRequestID);

        assertEquals(dummyContainerID, response.containerId);
        assertEquals(dummyContainerService, response.service);
    }

    // clearResponseFromBuffers
    @Test
    public void TestClearResponseFromBuffers() {
        Util.getResponseFromBuffer(dummyRequestID);

        ArrayList<String> responseBuffer = Util.getResponseBuffer();
        ArrayList<ReceivedEventInterface> responses = Util.getResponses();

        assertEquals(0, responseBuffer.size());
        assertEquals(0, responses.size());
    }
}
