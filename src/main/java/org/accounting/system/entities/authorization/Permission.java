package org.accounting.system.entities.authorization;

import org.accounting.system.enums.AccessType;
import org.accounting.system.enums.Operation;

/**
 * By combining {@link org.accounting.system.enums.Operation} and {@link org.accounting.system.enums.AccessType},
 * you can generate permissions for the Accounting System Collections and then you can assign these permissions to each role.
 */
public class Permission {

    public Operation operation;
    public AccessType accessType;
}
