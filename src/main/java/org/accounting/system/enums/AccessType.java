package org.accounting.system.enums;

import java.util.Arrays;
import java.util.List;

/**
 * The available access types are :
 * <ul>
 *   <li>ALWAYS -> User/Service is always able to perform the corresponding operation</li>
 *   <li>NEVER  -> User/Service is never able to perform the corresponding operation</li>
 *   <li>ENTITY -> User/Service has only access to entities that they have created</li>
 * </ul>
 *
 * If the service has more than one role :
 * <ul>
 *   <li>If the access types are Always and Entity, the service will have Always access, because this access type is the most permissive and will override the others. In other words, the most permissive access type takes precedence.</li>
 *   <li>Never always takes precedence over any other access type.</li>
 * </ul>
 */
public enum AccessType {

    NEVER(0, false),
    ALWAYS(1, true),
    ENTITY(2, true);

    public final int precedence;
    public final boolean access;

    AccessType(int precedence, boolean access) {
        this.precedence = precedence;
        this.access = access;
    }

    public static AccessType higherPrecedence(List<AccessType> accessTypeList){

        int precedence = accessTypeList
                .stream()
                .mapToInt(at->at.precedence)
                .min()
                .orElse(0);

        return valueOfPrecedence(precedence);
    }

    private static AccessType valueOfPrecedence(int precedence) {

        return Arrays
                .stream(values())
                .filter(accessType -> accessType.precedence == precedence)
                .findFirst()
                .get();
    }

}
