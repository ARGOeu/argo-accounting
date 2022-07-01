package org.accounting.system.enums.acl;

/**
 * Defines the permissions for use with the permissions component of an Access Control
 * {@link org.accounting.system.entities.acl.AccessControl access control}
 */
public enum AccessControlPermission {

    /**
     * Permission to read a particular entity.
     */
    READ,
    /**
     * Permission to update a particular entity.
     */
    UPDATE,
    /**
     * Permission to delete a particular entity.
     */
    DELETE,
    /**
     * Permission to grant acl to particular entity.
     */
    ACL,
    /**
     * Permission to associate Providers with a specific Project.
     */
    ASSOCIATE,
    /**
     * Permission to dissociate Providers from a specific Project.
     */
    DISSOCIATE,
    /**
     * Permission to register a Project from OpenAire.
     */
    REGISTER;
}
