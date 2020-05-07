package models;

import org.jongo.marshall.jackson.oid.MongoObjectId;

public class Response {
    @MongoObjectId
    public String _id;

    public String response;
}
