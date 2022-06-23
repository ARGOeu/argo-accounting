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
 *   <li>ACCESS_PROJECT -> Have rights to manage a Project</li>
 * </ul>
 *
 * We have collapsed the following Project operations into one operation called ACCESS_PROJECT:
 * - Register a Project
 * - Associate Providers with a Project
 * - Dissociate Providers from a Project
 * - Create Project Installations
 * - Assign Metrics to all Project Installations
 * - Read all Project Metrics
 *
 * We have collapsed the following Provider operations into one operation called ACCESS_PROVIDER:
 * - Create Provider Installations
 * - Assign Metrics to all Provider Installations
 * - Read all Provider Metrics
 */
public enum Operation {

    CREATE,
    UPDATE,
    DELETE,
    READ,
    ACL,
    ASSIGN_ROLE,
    DETACH_ROLE,
    ACCESS_PROJECT,
    ACCESS_PROVIDER;
}
