package org.accounting.system.services;

import io.quarkus.oidc.TokenIntrospection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.accounting.system.entitlements.Entitlement;
import org.accounting.system.entitlements.EntitlementUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ApplicationScoped
public class EntitlementService {

    @Inject
    TokenIntrospection tokenIntrospection;

    public boolean hasAccess(String group, String role, List<String> targetHierarchy) {

        if (targetHierarchy == null || targetHierarchy.isEmpty()) {

            return getEntitlements().stream()
                    .anyMatch(e -> e.getGroup().equals(group)
                            && e.getRole().equals(role)
                            && isSysAdmin());
        } else {

            return getEntitlements().stream()
                    .filter(e -> e.getGroup().equals(group) && e.getRole().equals(role))
                    .anyMatch(e -> isSysAdmin() || hierarchyCovers(e.getHierarchy(), targetHierarchy));
        }
    }

    private boolean hierarchyCovers(List<String> entitlementHierarchy, List<String> targetHierarchy) {
        if (entitlementHierarchy.size() > targetHierarchy.size()) return false;
        for (int i = 0; i < entitlementHierarchy.size(); i++) {
            if (!entitlementHierarchy.get(i).equals(targetHierarchy.get(i))) return false;
        }
        return true;
    }

    public boolean isSysAdmin() {
        return getEntitlements().stream()
                .anyMatch(e -> "admin".equals(e.getRole()) && !e.hasHierarchy());
    }

    private List<Entitlement> getEntitlements(){

        var rawEntitlements = tokenIntrospection.getJsonObject().getJsonArray("entitlements");

        if(Objects.isNull(rawEntitlements)){

            return Collections.emptyList();
        } else {

            var entitlements = rawEntitlements.stream()
                    .map(v -> v.toString().replace("\"", ""))
                    .collect(Collectors.toList());

            return EntitlementUtils.parseEntitlements(entitlements);
        }
    }
}
