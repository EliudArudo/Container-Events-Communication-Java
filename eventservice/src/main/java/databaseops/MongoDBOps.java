package databaseops;

import interfaces.EventInterface;
import interfaces.InitialisedRecordInfoInterface;

public class MongoDBOps {

    public static InitialisedRecordInfoInterface recordNewTaskInDB(EventInterface task) {
       return new InitialisedRecordInfoInterface();
    }

    public static EventInterface completeExistingTaskRecordInDB(EventInterface funcResponse) {
       return new EventInterface();
    }
}
