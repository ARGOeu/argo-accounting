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

    @ConfigProperty(name = "key.to.retrieve.id.from.access.token")
    String key;

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

            return tokenIntrospection.getJsonObject().getString(key);

        } catch (Exception e) {

            throw new BadRequestException(String.format("The unique identifier %s wasn't present in the access token.", key));
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
