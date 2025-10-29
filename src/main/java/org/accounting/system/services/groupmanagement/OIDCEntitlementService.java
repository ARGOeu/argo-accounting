package org.accounting.system.services.groupmanagement;

import io.quarkus.oidc.TokenIntrospection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.accounting.system.beans.RequestUserContext;
import org.accounting.system.entitlements.Entitlement;
import org.accounting.system.entitlements.EntitlementUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ApplicationScoped
public class OIDCEntitlementService implements EntitlementService {

    @Inject
    TokenIntrospection tokenIntrospection;

    @Inject
    RequestUserContext requestUserContext;

    public List<Entitlement> fetchEntitlements(){

        var rawEntitlements = tokenIntrospection.getJsonObject().getJsonArray("entitlements");

        if(Objects.isNull(rawEntitlements)){

            return Collections.emptyList();
        } else {

            var entitlements = rawEntitlements
                    .stream()
                    .map(v -> v.toString().replace("\"", ""))
                    .filter(s->s.startsWith(requestUserContext.getNamespace()))
                    .collect(Collectors.toList());

            return EntitlementUtils.parseEntitlements(entitlements);
        }
    }
}
