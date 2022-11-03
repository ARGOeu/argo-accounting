package org.accounting.system.enums;

/**
 * The available API operations are :
 * <ul>
 *   <li>CREATE -> Can create new entities within a collection</li>
 *   <li>UPDATE -> Can update existing entities within a collection</li>
 *   <li>DELETE -> Can delete existing entities within a collection</li>
 *   <li>READ   -> Can fetch existing entities within a collection</li>
 *   <li>ACL    -> Can grant acl permission to existing entities within a collection</>
 *   <li>ASSIGN_ROLE -> Can assign roles to a registered client</li>
 *   <li>DETACH_ROLE -> Can detach roles from a registered client</li>
 * </ul>
 */
public enum Operation {

    CREATE,
    UPDATE,
    DELETE,
    READ,
    ACL,
    ASSIGN_ROLE,
    DETACH_ROLE,
    ASSOCIATE,
    DISSOCIATE;
}
