package interfaces;

public class MongoDBEnvInterface {

    private String uri;
    private int port;
    public String database;

    public MongoDBEnvInterface(String uri, int port, String database) {
        this.uri = uri;
        this.port = port;
        this.database = database;
    }

    public String getFullURI() {
        String joinedURI = "mongodb://" + uri + ":" + port + "/" + database;
        return joinedURI;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
