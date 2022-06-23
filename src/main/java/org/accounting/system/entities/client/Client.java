package org.accounting.system.entities.client;

import org.accounting.system.entities.Entity;
import org.bson.codecs.pojo.annotations.BsonId;

import java.util.Set;

public class Client extends Entity {

    @BsonId
    private String id;

    private String name;

    private String email;

    private boolean systemAdmin;

    private Set<String> roles;

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

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public boolean isSystemAdmin() {
        return systemAdmin;
    }

    public void setSystemAdmin(boolean systemAdmin) {
        this.systemAdmin = systemAdmin;
    }
}
