package interfaces;

public class InitialisedRecordInfoInterface {

    public String containerId;
    public String containerService;
    public String recordId;
    public TASK_TYPE task;
    public SUB_TASK_TYPE subtask;
    public String requestBody;
    public String serviceContainerId;
    public String serviceContainerService;

    public Boolean existing;
    public String responseBody;
    public String chosenContainerId;
    public String chosenContainerService;


    public InitialisedRecordInfoInterface() {}

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
