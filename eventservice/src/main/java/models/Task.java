package models;

import org.jongo.marshall.jackson.oid.MongoId;
import org.jongo.marshall.jackson.oid.MongoObjectId;

public class Task {
    @MongoId // auto
    @MongoObjectId
    private String _id;

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

    public Task() {}

    public Task(Task task) {
        this.setTask(task);
    }

    public String getId() {
        return this._id;
    }

    private void setTask(Task task) {
        this.fromRequestId = task.fromRequestId;
        this.fromContainerId = task.fromContainerId;
        this.fromContainerService = task.fromContainerService;
        this.fromReceivedTime = task.fromReceivedTime;
        this.task = task.task;
        this.subtask = task.subtask;
        this.requestBodyId = task.requestBodyId;
        this.toContainerId = task.toContainerId;
        this.toContainerService = task.toContainerService;
        this.serviceContainerId = task.serviceContainerId;
        this.serviceContainerService = task.serviceContainerService;

        this.toReceivedTime = task.toReceivedTime;
        this.toResponseBodyId = task.toResponseBodyId;
        this.fromSentTime = task.fromSentTime;
    }
}
