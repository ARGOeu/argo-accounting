package org.accounting.system.beans;

import io.quarkus.oidc.TokenIntrospection;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import lombok.Getter;
import lombok.Setter;
import org.accounting.system.enums.AccessType;
import org.accounting.system.repositories.OidcTenantConfigRepository;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Optional;

@RequestScoped
public class RequestUserContext {

    @ConfigProperty(name = "person.key.to.retrieve.id.from.access.token")
    @Getter
    String personKey;

    @ConfigProperty(name = "service.key.to.retrieve.id.from.access.token")
    @Getter
    String serviceKey;

    @Inject
    private final TokenIntrospection tokenIntrospection;

    @Inject
    private final OidcTenantConfigRepository oidcTenantConfigRepository;

    @Getter
    @Setter
    private AccessType accessType;

    public RequestUserContext(TokenIntrospection tokenIntrospection, OidcTenantConfigRepository oidcTenantConfigRepository) {

        this.tokenIntrospection = tokenIntrospection;
        this.oidcTenantConfigRepository = oidcTenantConfigRepository;
    }

    public String getId(){

        var optional = oidcTenantConfigRepository.fetchOidcTenantConfigByIssuer(tokenIntrospection.getJsonObject().getString("iss"));

        if(optional.isPresent()){

            var config = optional.get();

            personKey = config.getUserIdTokenClaim();
            serviceKey = config.getServiceIdTokenClaim();
        }

        try {

            return tokenIntrospection.getJsonObject().getString(personKey);

        } catch (Exception e) {

            try{

                return tokenIntrospection.getJsonObject().getString(serviceKey);

            } catch (Exception ex){

                throw new BadRequestException(String.format("Unique identifiers [%s, %s] weren't present in the access token.", personKey, serviceKey));
            }
        }
    }

    public Optional<String> getName(){

        try {

            return Optional.of(tokenIntrospection.getJsonObject().getString("name"));

        } catch (Exception e) {

            return Optional.empty();
        }
    }

    public Optional<String> getEmail(){

        try {

            return Optional.of(tokenIntrospection.getJsonObject().getString("email"));

        } catch (Exception e) {

            return Optional.empty();
        }
    }

    public String getIssuer(){

        return tokenIntrospection.getJsonObject().getString("iss");
    }
}
