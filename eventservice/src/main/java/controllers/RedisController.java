package controllers;

import dockerapi.ContainerInfo;
import logic.Logic;

public class RedisController {
    public static void redisControllerSetup(String sentEvent, ContainerInfo functionContainerInfo) {
        Logic.eventDeterminer(sentEvent, functionContainerInfo);
    }
}
