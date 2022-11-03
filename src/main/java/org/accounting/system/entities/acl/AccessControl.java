package org.accounting.system.entities.acl;

import lombok.EqualsAndHashCode;
import org.accounting.system.entities.Entity;

/**
 * An access control (AC) is a list of rules that specifies which clients are granted access to particular entities.
 */

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public abstract class AccessControl extends Entity {

    /**
     *{@link #getWho() who} is the id of a client that the AccessControl grants access.
     */
    @EqualsAndHashCode.Include
    private String who;

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }
}