package initialize;

import dockerapi.ContainerInfo;
import interfaces.ContainerInfoInterface;

public class DockerAPIInit {
    public static void initialFetchMyContainerInfo(ContainerInfo containerInfo) {
        ContainerInfoInterface myContainerInfo = containerInfo.fetchContainerInfo();

        // Logging stuff here
    }
}
