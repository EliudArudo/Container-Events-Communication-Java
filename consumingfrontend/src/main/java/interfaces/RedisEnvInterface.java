package interfaces;

public class RedisEnvInterface {
    public String REDIS_HOST;
    public String REDIS_PORT;

    public RedisEnvInterface(String host, String port) {
        this.REDIS_HOST = host;
        this.REDIS_PORT = port;
    }
}
