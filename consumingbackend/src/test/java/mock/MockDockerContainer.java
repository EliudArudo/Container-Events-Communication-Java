package mock;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ContainerMount;
import com.spotify.docker.client.messages.NetworkSettings;

import java.util.HashMap;
import java.util.Map;

public class MockDockerContainer extends Container  {

    private String id;
    private String service;

    public MockDockerContainer(String id, String service){
        this.id = id;
        this.service = service;
    };

    @Override
    public String id() {
        return id;
    }

    @Override
    public ImmutableList<String> names() {
        return null;
    }

    @Override
    public String image() {
        return null;
    }

    @Override
    public String imageId() {
        return null;
    }

    @Override
    public String command() {
        return null;
    }

    @Override
    public Long created() {
        return null;
    }

    @Override
    public String state() {
        return null;
    }

    @Override
    public String status() {
        return null;
    }

    @Override
    public ImmutableList<PortMapping> ports() {
        return null;
    }

    @Override
    public ImmutableMap<String, String> labels() {
        Map<String, String> mutableMap = new HashMap<>();
        mutableMap.put("com.docker.swarm.service.name", service);

        ImmutableMap<String, String> labelList = ImmutableMap.copyOf(mutableMap);

        return labelList;
    }

    @Override
    public Long sizeRw() {
        return null;
    }

    @Override
    public Long sizeRootFs() {
        return null;
    }

    @Override
    public NetworkSettings networkSettings() {
        return null;
    }

    @Override
    public ImmutableList<ContainerMount> mounts() {
        return null;
    }

}
