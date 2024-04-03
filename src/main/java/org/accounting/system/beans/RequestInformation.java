package org.accounting.system.beans;

import jakarta.enterprise.context.RequestScoped;
import org.accounting.system.enums.AccessType;


@RequestScoped
public class RequestInformation {

    private String subjectOfToken;
    private AccessType accessType;

    public String getSubjectOfToken() {
        return subjectOfToken;
    }

    public void setSubjectOfToken(String subjectOfToken) {
        this.subjectOfToken = subjectOfToken;
    }

    public AccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(AccessType accessType) {
        this.accessType = accessType;
    }
}
