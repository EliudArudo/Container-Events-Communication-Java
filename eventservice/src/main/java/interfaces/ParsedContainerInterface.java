package interfaces;

public class ParsedContainerInterface {
    public String containerID;
    public String containerService;

    public ParsedContainerInterface(String containerID, String containerService) {
        this.containerID = containerID;
        this.containerService = containerService;
    }
}
