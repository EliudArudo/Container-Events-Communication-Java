package initialize;

import dockerapi.ContainerInfo;
import interfaces.STATUS_TYPE;
import log.Logging;

public class DockerAPIInit {
    private static String packageName = "initialize::DockerAPIInit";

    private static ContainerInfo containerInfo;

    public DockerAPIInit() {}

    public static void initialFetchMyContainerInfo() {
        try {
            containerInfo = new ContainerInfo();

            containerInfo.fetchContainerInfo();
        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "initialFetchMyContainerInfo", e.getMessage());
        }
    }

    public static ContainerInfo getContainerInfoInstance() {
        return containerInfo;
    }
}
