package org.accounting.system.beans;

import io.quarkus.oidc.TokenIntrospection;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import lombok.Getter;
import lombok.Setter;
import org.accounting.system.enums.AccessType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@RequestScoped
public class RequestUserContext {

    @ConfigProperty(name = "person.key.to.retrieve.id.from.access.token")
    String personKey;

    @ConfigProperty(name = "service.key.to.retrieve.id.from.access.token")
    String serviceKey;

    @Inject
    private final TokenIntrospection tokenIntrospection;

    @Getter
    @Setter
    private AccessType accessType;

    public RequestUserContext(TokenIntrospection tokenIntrospection) {

        this.tokenIntrospection = tokenIntrospection;
    }

    public String getId(){

        try {

            return tokenIntrospection.getJsonObject().getString(personKey);

        } catch (Exception e) {

            try {

                return tokenIntrospection.getJsonObject().getString(serviceKey);

            } catch (Exception exc) {

                throw new BadRequestException(String.format("None of the following unique identifiers were present in the access token : %s , %s", personKey, serviceKey));
            }
        }
    }
}
