package org.accounting.system.beans;

import io.quarkus.oidc.TokenIntrospection;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import lombok.Getter;
import org.accounting.system.entities.OidcTenantConfig;
import org.accounting.system.entities.Setting;
import org.accounting.system.enums.APISetting;
import org.accounting.system.repositories.OidcTenantConfigRepository;
import org.accounting.system.repositories.SettingRepository;
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

    @ConfigProperty(name = "api.group.management.namespace")
    String namespace;

    @ConfigProperty(name = "api.group.management.parent")
    String parent;

    @Inject
    TokenIntrospection tokenIntrospection;

    @Inject
    OidcTenantConfigRepository oidcTenantConfigRepository;

    @Inject
    SettingRepository settingRepository;

    public String getId() {

        var optional = oidcTenantConfigRepository.fetchOidcTenantConfigByIssuer(getIssuer());

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

    public String getIssuer(){

        return tokenIntrospection.getJsonObject().getString("iss");
    }

    public String getNamespace(){

        var optionalTenant = getOidcTenantConfig();

        if(optionalTenant.isEmpty()){

            return namespace;
        } else {

            return optionalTenant.get().getGroupManagementNamespace();
        }
    }

    public String getParent(){

        var optionalTenant = getOidcTenantConfig();

        if(optionalTenant.isEmpty()){

            return parent;
        } else {

            return optionalTenant.get().getGroupManagementParent();
        }
    }

    public Optional<OidcTenantConfig> getOidcTenantConfig(){

        return oidcTenantConfigRepository.fetchOidcTenantConfigByIssuer(getIssuer());
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

    public String entitlementManagement(){

        var optionalTenant = getOidcTenantConfig();

        if(optionalTenant.isEmpty()){

            return settingRepository.findByKey(APISetting.ENTITLEMENTS_MANAGEMENT)
                    .map(Setting::getValue)
                    .orElse(APISetting.ENTITLEMENTS_MANAGEMENT.getDefaultValue());
        } else {

            return optionalTenant.get().getEntitlementManagement();
        }
    }
}
