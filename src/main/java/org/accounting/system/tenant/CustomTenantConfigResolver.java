package org.accounting.system.tenant;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.oidc.OidcRequestContext;
import io.quarkus.oidc.OidcTenantConfig;
import io.quarkus.oidc.TenantConfigResolver;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ServerErrorException;
import org.accounting.system.repositories.OidcTenantConfigRepository;

import java.util.Base64;
import java.util.Map;

@ApplicationScoped
public class CustomTenantConfigResolver implements TenantConfigResolver {

    @Inject
    OidcTenantConfigRepository oidcTenantConfigRepository;

    @Override
    public Uni<OidcTenantConfig> resolve(RoutingContext routingContext, OidcRequestContext<OidcTenantConfig> requestContext) {

        try {
            return Uni.createFrom().item(createTenantConfig(routingContext));
        } catch (JsonProcessingException e) {
            throw new ServerErrorException(e.getMessage(), 500);
        }
    }

    private OidcTenantConfig createTenantConfig(RoutingContext context) throws JsonProcessingException {

        var mapper = new ObjectMapper();

        var authHeader = context.request().getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        var token = authHeader.substring("Bearer ".length());

        var parts = token.split("\\.");
        if (parts.length < 2) {
            return null;
        }

        var payloadJsonStr = new String(Base64.getUrlDecoder().decode(parts[1]));
        var claims = mapper.readValue(payloadJsonStr, Map.class);
        var issuer = (String) claims.get("iss");
        if (issuer == null) {
            throw new BadRequestException("Missing 'iss' claim in access token.");
        }

        var optionalTenant = oidcTenantConfigRepository.fetchOidcTenantConfigByIssuer(issuer);

        if(optionalTenant.isEmpty()){

            return null;
        } else {

            var tenant = optionalTenant.get();

            final var config = new OidcTenantConfig();
            config.setAuthServerUrl(tenant.getAuthServerUrl());
            config.setTenantId(tenant.getTenantId());
            config.setClientId(tenant.getClientId());
            config.setCredentials(new OidcTenantConfig.Credentials());
            config.getCredentials().setSecret(tenant.getSecret());
            config.setDiscoveryEnabled(false);
            config.setAuthorizationPath(tenant.getAuthorizationPath());
            config.setIntrospectionPath(tenant.getIntrospectionPath());
            config.setUserInfoPath(tenant.getUserInfoPath());
            config.setTokenPath(tenant.getTokenPath());
            return config;
        }
    }
}