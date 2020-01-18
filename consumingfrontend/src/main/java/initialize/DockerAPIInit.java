package initialize;

import dockerapi.ContainerInfo;

public class DockerAPIInit {

    private static ContainerInfo containerInfo;

    public DockerAPIInit() {}

    public static void initialFetchMyContainerInfo() {
        containerInfo = new ContainerInfo();

        containerInfo.fetchContainerInfo();
        // Logging stuff here
    }

    public static ContainerInfo getContainerInfoInstance() {
        return containerInfo;
    }
}
