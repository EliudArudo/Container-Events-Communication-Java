package models;

import org.jongo.marshall.jackson.oid.MongoObjectId;

public class Task {
    @MongoObjectId
    public String _id;

    public String fromRequestId;
    public String fromContainerId;
    public String fromContainerService;
    public String fromReceivedTime;
    public String task;
    public String subtask;
    public String requestBodyId;
    public String toContainerId;
    public String toContainerService;
    public String serviceContainerId;
    public String serviceContainerService;

    public String toReceivedTime;
    public String toResponseBodyId;
    public String fromSentTime;
}
