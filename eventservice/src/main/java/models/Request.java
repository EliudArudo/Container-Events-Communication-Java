package models;

import org.jongo.marshall.jackson.oid.MongoObjectId;

public class Request {
    @MongoObjectId
    public String _id;

    public String request;

}
