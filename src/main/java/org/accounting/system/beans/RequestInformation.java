package org.accounting.system.beans;

import org.accounting.system.enums.AccessType;
import org.accounting.system.interceptors.annotations.AccessPermissionsUtil;

import javax.enterprise.context.RequestScoped;
import java.util.List;

@RequestScoped
public class RequestInformation {

    private String subjectOfToken;
    private AccessType accessType;
    private List<AccessPermissionsUtil> accessPermissions;

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

    public List<AccessPermissionsUtil> getAccessPermissions() {
        return accessPermissions;
    }

    public void setAccessPermissions(List<AccessPermissionsUtil> accessPermissions) {
        this.accessPermissions = accessPermissions;
    }
}
