package org.accounting.system.services.groupmanagement;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.accounting.system.beans.RequestUserContext;
import org.accounting.system.entitlements.Entitlement;
import org.accounting.system.entitlements.EntitlementUtils;
import org.accounting.system.repositories.groupmanagement.ActorEntitlementsRepository;
import org.accounting.system.repositories.groupmanagement.ActorRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class DatabaseEntitlementService implements EntitlementService {

    @Inject
    RequestUserContext requestUserContext;

    @Inject
    ActorRepository actorRepository;

    @Inject
    ActorEntitlementsRepository actorEntitlementsRepository;

    @Override
    public List<Entitlement> fetchEntitlements() {

        var actor = actorRepository.findByOidcIdAndIssuer(requestUserContext.getId(), requestUserContext.getIssuer());

        if(actor.isEmpty()){

            return Collections.emptyList();
        } else {

            var rawEntitlements = actorEntitlementsRepository.findActorEntitlements(actor.get().getId());

            if (rawEntitlements.isEmpty()) {

                return Collections.emptyList();
            } else {

                var entitlements = rawEntitlements
                        .stream()
                        .filter(s -> s.startsWith(requestUserContext.getNamespace()))
                        .collect(Collectors.toList());

                return EntitlementUtils.parseEntitlements(entitlements);
            }
        }
    }
}
