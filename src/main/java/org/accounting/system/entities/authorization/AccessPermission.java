package org.accounting.system.entities.authorization;

import lombok.EqualsAndHashCode;
import org.accounting.system.enums.AccessType;
import org.accounting.system.enums.Operation;
import org.bson.codecs.pojo.annotations.BsonProperty;

/**
 * By combining {@link Operation} and {@link org.accounting.system.enums.AccessType},
 * you can generate access permissions for the Accounting System Collections and then you can assign these permissions to each role.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AccessPermission {

    @EqualsAndHashCode.Include
    public Operation operation;

    @BsonProperty("access_type")
    @EqualsAndHashCode.Include
    public AccessType accessType;
}
