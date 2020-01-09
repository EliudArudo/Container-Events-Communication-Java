package interfaces;

public class TaskInterface {
    public TASK_TYPE task;
    public SUB_TASK_TYPE subtask;
    public String containerId;
    public String service;
    public String requestId;
    public String requestBody; // JSON

    public String serviceContainerId;
    public String serviceContainerService;

    public TaskInterface(
            TASK_TYPE task,
            SUB_TASK_TYPE subtask,
            String containerId,
            String service,
            String requestId,
            String requestBody,
            String serviceContainerId,
            String serviceContainerService
    ) {
        this.task = task;
        this.subtask = subtask;
        this.containerId = containerId;
        this.service = service;
        this.requestId = requestId;
        this.requestBody = requestBody;
        this.serviceContainerId = serviceContainerId;
        this.serviceContainerService = serviceContainerService;
    }
}
