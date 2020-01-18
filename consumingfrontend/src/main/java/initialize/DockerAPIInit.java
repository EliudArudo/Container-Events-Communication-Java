package initialize;

import com.google.gson.Gson;
import dockerapi.ContainerInfo;
import interfaces.ContainerInfoInterface;
import interfaces.STATUS_TYPE;
import log.Logging;

public class DockerAPIInit {
    private static String packageName = "initialize::DockerAPIInit";

    private static ContainerInfo containerInfo;

    public DockerAPIInit() {}

    public static void initialFetchMyContainerInfo() {
        try {
            containerInfo = new ContainerInfo();

            ContainerInfoInterface fetchedContainerInfo = containerInfo.fetchContainerInfo();

            String containerInfoJSON = new Gson().toJson(fetchedContainerInfo);

            Logging.logStatusFileMessage(STATUS_TYPE.Success, packageName,"initialFetchMyContainerInfo", "myContainerInfo:" + containerInfoJSON);
        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "initialFetchMyContainerInfo", e.getMessage());
        }
    }

    public static ContainerInfo getContainerInfoInstance() {
        return containerInfo;
    }
}
