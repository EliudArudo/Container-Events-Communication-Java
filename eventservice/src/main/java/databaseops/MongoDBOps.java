package databaseops;

import interfaces.EventInterface;
import interfaces.InitialisedRecordInfoInterface;

// TODO - Continue from here
// Create a CRUD wrapper of mongodb default driver
public class MongoDBOps {

    public static InitialisedRecordInfoInterface recordNewTaskInDB(EventInterface task) {
       return new InitialisedRecordInfoInterface();
    }

    public static EventInterface completeExistingTaskRecordInDB(EventInterface funcResponse) {
       return new EventInterface();
    }
}
