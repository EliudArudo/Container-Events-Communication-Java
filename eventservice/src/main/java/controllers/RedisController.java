package controllers;

import dockerapi.DockerAPI;
import logic.Logic;

public class RedisController {
    public static void redisControllerSetup(String sentEvent, DockerAPI functionDockerAPI) {
        Logic.eventDeterminer(sentEvent, functionDockerAPI);
    }
}
