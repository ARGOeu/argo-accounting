package org.accounting.system.entities.client;

import org.accounting.system.entities.Entity;
import org.bson.codecs.pojo.annotations.BsonId;

public class Client extends Entity {

    @BsonId
    private String id;

    private String name;

    private String email;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
