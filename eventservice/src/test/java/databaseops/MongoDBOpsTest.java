package databaseops;

import interfaces.EventInterface;
import models.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class MongoDBOpsTest {

    @Test
    public void TestGetParsedResponse () {
        Task dummyOldTask = new Task();
        dummyOldTask.fromRequestId = "dummyFromRequestId";
        dummyOldTask.fromRequestId = "dummyFromContainerId";
        dummyOldTask.fromRequestId = "dummyFromContainerService";

        EventInterface dummyResponse = new EventInterface();
        dummyResponse.responseBody = "dummyResponseBody";

        EventInterface response = MongoDBOps.getParsedResponse(dummyResponse, dummyOldTask);

        assertEquals(response.requestId, dummyOldTask.fromRequestId);
        assertEquals(response.containerId, dummyOldTask.fromContainerId);
        assertEquals(response.service, dummyOldTask.fromContainerService);
        assertEquals(response.responseBody, dummyResponse.responseBody);
    }
}
