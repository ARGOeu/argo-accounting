package org.accounting.system.entities;

import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
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

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getAuthorizationPath() {
        return authorizationPath;
    }

    public void setAuthorizationPath(String authorizationPath) {
        this.authorizationPath = authorizationPath;
    }

    public String getIntrospectionPath() {
        return introspectionPath;
    }

    public void setIntrospectionPath(String introspectionPath) {
        this.introspectionPath = introspectionPath;
    }

    public String getUserInfoPath() {
        return userInfoPath;
    }

    public void setUserInfoPath(String userInfoPath) {
        this.userInfoPath = userInfoPath;
    }

    public String getTokenPath() {
        return tokenPath;
    }

    public void setTokenPath(String tokenPath) {
        this.tokenPath = tokenPath;
    }

    public String getAuthServerUrl() {
        return authServerUrl;
    }

    public void setAuthServerUrl(String authServerUrl) {
        this.authServerUrl = authServerUrl;
    }

    public String getUserIdTokenClaim() {
        return userIdTokenClaim;
    }

    public void setUserIdTokenClaim(String userIdTokenClaim) {
        this.userIdTokenClaim = userIdTokenClaim;
    }

    public String getServiceIdTokenClaim() {
        return serviceIdTokenClaim;
    }

    public void setServiceIdTokenClaim(String serviceIdTokenClaim) {
        this.serviceIdTokenClaim = serviceIdTokenClaim;
    }
}
