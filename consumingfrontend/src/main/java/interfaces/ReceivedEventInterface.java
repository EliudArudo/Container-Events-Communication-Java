package interfaces;


public class ReceivedEventInterface implements Cloneable {
    public String requestId;
    public String containerId;
    public String service;

    // Received user>'this_container'>'event'>'service'>'event'>'this_container'
    public String responseBody; // JSON

    // Received 'event'>'this_container'
    public String recordId;
    public TASK_TYPE task;
    public SUB_TASK_TYPE subtask;

    public String requestBody;

    public String serviceContainerId;
    public String serviceContainerService;

    public ReceivedEventInterface(){};

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
