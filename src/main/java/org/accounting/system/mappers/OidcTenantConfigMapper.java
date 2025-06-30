package org.accounting.system.mappers;

import jakarta.enterprise.inject.spi.CDI;
import org.accounting.system.beans.RequestUserContext;
import org.accounting.system.dtos.tenant.OidcTenantConfigRequest;
import org.accounting.system.dtos.tenant.OidcTenantConfigResponse;
import org.accounting.system.dtos.tenant.UpdateOidcTenantConfig;
import org.accounting.system.entities.OidcTenantConfig;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(imports = StringUtils.class)
public interface OidcTenantConfigMapper {

    OidcTenantConfigMapper INSTANCE = Mappers.getMapper(OidcTenantConfigMapper.class);

    @Named("configToDto")
    @Mapping( target="id", expression="java(config.getId().toString())")
    OidcTenantConfigResponse configToDto(OidcTenantConfig config);

    OidcTenantConfig dtoToConfig(OidcTenantConfigRequest request);

    @IterableMapping(qualifiedByName = "configToDto")
    List<OidcTenantConfigResponse> configsToResponse(List<OidcTenantConfig> configs);

    @Mapping(target = "clientId", expression = "java(StringUtils.isNotEmpty(request.clientId) ? request.clientId : oidcTenantConfig.getClientId())")
    @Mapping(target = "authorizationPath", expression = "java(StringUtils.isNotEmpty(request.authorizationPath) ? request.authorizationPath : oidcTenantConfig.getAuthorizationPath())")
    @Mapping(target = "introspectionPath", expression = "java(StringUtils.isNotEmpty(request.introspectionPath) ? request.introspectionPath : oidcTenantConfig.getIntrospectionPath())")
    @Mapping(target = "userInfoPath", expression = "java(StringUtils.isNotEmpty(request.userInfoPath) ? request.userInfoPath : oidcTenantConfig.getUserInfoPath())")
    @Mapping(target = "tokenPath", expression = "java(StringUtils.isNotEmpty(request.tokenPath) ? request.tokenPath : oidcTenantConfig.getTokenPath())")
    @Mapping(target = "authServerUrl", expression = "java(StringUtils.isNotEmpty(request.authServerUrl) ? request.authServerUrl : oidcTenantConfig.getAuthServerUrl())")
    @Mapping(target = "secret", expression = "java(StringUtils.isNotEmpty(request.secret) ? request.secret : oidcTenantConfig.getSecret())")
    @Mapping(target = "tenantId", expression = "java(StringUtils.isNotEmpty(request.tenantId) ? request.tenantId : oidcTenantConfig.getTenantId())")
    @Mapping(target = "issuer", expression = "java(StringUtils.isNotEmpty(request.issuer) ? request.issuer : oidcTenantConfig.getIssuer())")
    @Mapping(target = "userIdTokenClaim", expression = "java(StringUtils.isNotEmpty(request.userIdTokenClaim) ? request.userIdTokenClaim : oidcTenantConfig.getUserIdTokenClaim())")
    @Mapping(target = "serviceIdTokenClaim", expression = "java(StringUtils.isNotEmpty(request.serviceIdTokenClaim) ? request.serviceIdTokenClaim : oidcTenantConfig.getServiceIdTokenClaim())")
    void updateConfigFromDto(UpdateOidcTenantConfig request, @MappingTarget OidcTenantConfig oidcTenantConfig);

    @AfterMapping
    default void setCreatorId(OidcTenantConfigRequest request, @MappingTarget OidcTenantConfig config) {
        var requestInformation = CDI.current().select(RequestUserContext.class).get();
        config.setCreatorId(requestInformation.getId());
    }
}
