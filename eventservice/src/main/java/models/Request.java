package models;

import org.jongo.marshall.jackson.oid.MongoId;
import org.jongo.marshall.jackson.oid.MongoObjectId;

public class Request {
    @MongoId // auto
    @MongoObjectId
    private String _id;

    public String request;

    public String getId() {
        return this._id;
    }

}
