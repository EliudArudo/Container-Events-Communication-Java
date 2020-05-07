package initialize;

import com.google.gson.Gson;
import dockerapi.DockerAPI;
import interfaces.ContainerInfoInterface;
import interfaces.STATUS_TYPE;
import log.Logging;

public class DockerAPIInit {
    private static String packageName = "initialize::DockerAPIInit";

    private static DockerAPI dockerAPI;

    public DockerAPIInit() {}

    public static void initialFetchMyContainerInfo() {
        try {
            dockerAPI = new DockerAPI();

            ContainerInfoInterface fetchedContainerInfo = dockerAPI.fetchContainerInfo();

            String containerInfoJSON = new Gson().toJson(fetchedContainerInfo);

            Logging.logStatusFileMessage(STATUS_TYPE.Success, packageName,"initialFetchMyContainerInfo", "myContainerInfo:" + containerInfoJSON);
        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "initialFetchMyContainerInfo", e.getMessage());
        }
    }

    public static DockerAPI getContainerInfoInstance() {
        return dockerAPI;
    }
}
