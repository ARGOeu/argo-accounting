package org.accounting.system.services.groupmanagement;

import org.accounting.system.entitlements.Entitlement;

import java.util.List;

public interface EntitlementService {

    default boolean hasAccess(String group, String role, List<String> targetHierarchy) {

        if (targetHierarchy == null || targetHierarchy.isEmpty()) {

            return fetchEntitlements().stream()
                    .anyMatch(e -> e.getGroup().equals(group)
                            && e.getRole().equals(role)
                            && isSysAdmin());
        } else {

            return fetchEntitlements().stream()
                    .filter(e -> e.getGroup().equals(group) && e.getRole().equals(role))
                    .anyMatch(e -> isSysAdmin() || hierarchyCovers(e.getHierarchy(), targetHierarchy));
        }
    }

    default boolean hierarchyCovers(List<String> entitlementHierarchy, List<String> targetHierarchy) {

        if (entitlementHierarchy.size() > targetHierarchy.size()) return false;
        for (int i = 0; i < entitlementHierarchy.size(); i++) {
            if (!entitlementHierarchy.get(i).equals(targetHierarchy.get(i))) return false;
        }

        return true;
    }

    default boolean isSysAdmin() {

        return fetchEntitlements().stream()
                .anyMatch(e -> "admin".equals(e.getRole()) && !e.hasHierarchy());
    }

    List<Entitlement> fetchEntitlements();
}
