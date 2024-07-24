package org.accounting.system.beans;

import io.quarkus.oidc.TokenIntrospection;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import lombok.Getter;
import lombok.Setter;
import org.accounting.system.enums.AccessType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Optional;

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
}
