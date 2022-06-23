package org.accounting.system.interceptors.annotations;

import org.accounting.system.enums.AccessType;
import org.accounting.system.enums.Collection;

public class AccessPermissionsUtil {

    private Collection collection;

    private AccessType accessType;

    private int permissionPrecedence;

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public AccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(AccessType accessType) {
        this.accessType = accessType;
    }

    public int getPermissionPrecedence() {
        return permissionPrecedence;
    }

    public void setPermissionPrecedence(int permissionPrecedence) {
        this.permissionPrecedence = permissionPrecedence;
    }
}
