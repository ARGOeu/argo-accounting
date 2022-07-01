package org.accounting.system.beans;

import org.accounting.system.enums.AccessType;

import javax.enterprise.context.RequestScoped;

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
