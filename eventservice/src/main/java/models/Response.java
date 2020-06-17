package models;

import org.jongo.marshall.jackson.oid.MongoId;
import org.jongo.marshall.jackson.oid.MongoObjectId;

public class Response {
    @MongoId // auto
    @MongoObjectId
    private String _id;

    public String response;

    public String getId() {
        return this._id;
    }
}
