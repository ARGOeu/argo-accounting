package org.accounting.system.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Getter
@Setter
public class OidcTenantConfig extends Entity{

    private ObjectId id;

    private String issuer;

    @EqualsAndHashCode.Include
    private String tenantId;

    private String clientId;

    private String secret;

    private String authorizationPath;

    private String introspectionPath;

    private String userInfoPath;

    private String tokenPath;

    private String authServerUrl;

    private String userIdTokenClaim;

    private String serviceIdTokenClaim;

    private String groupManagementNamespace;

    private String groupManagementParent;

    private String entitlementManagement;
}
