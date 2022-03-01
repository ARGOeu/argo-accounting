package org.accounting.system.enums;

/**
 * The available API operations are :
 * <ul>
 *   <li>CREATE -> Can create new entities within a collection</li>
 *   <li>UPDATE -> Can update existing entities within a collection</li>
 *   <li>DELETE -> Can delete existing entities within a collection</li>
 *   <li>READ   -> Can fetch existing entities within a collection</li>
 * </ul>
 */
public enum Operation {

    CREATE,
    UPDATE,
    DELETE,
    READ;
}
