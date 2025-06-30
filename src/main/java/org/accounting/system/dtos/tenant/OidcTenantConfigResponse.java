package org.accounting.system.dtos.tenant;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;


@Schema(name="OidcTenantConfig", description="An object represents the Oidc Tenant Configuration.")
public class OidcTenantConfigResponse {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Unique ID of the Oidc Tenant Configuration.",
            example = "507f1f77bcf86cd799439011"
    )
    @JsonProperty("id")
    public String id;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Issuer URI for the tokens (often the same as `authServerUrl`).",
            example = "http://localhost:58080/realms/quarkus"
    )
    @JsonProperty("issuer")
    public String issuer;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Unique identifier for the tenant.",
            example = "default"
    )
    @JsonProperty("tenant_id")
    public String tenantId;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Client ID registered with the identity provider.",
            example = "backend-service"
    )
    @JsonProperty("client_id")
    public String clientId;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Client secret for authenticating the client.",
            example = "secret"
    )
    @JsonProperty("secret")
    public String secret;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Path to the authorization endpoint.",
            example = "/protocol/openid-connect/auth"
    )
    @JsonProperty("authorization_path")
    public String authorizationPath;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Path to the token introspection endpoint.",
            example = "/protocol/openid-connect/token/introspect"
    )
    @JsonProperty("introspection_path")
    public String introspectionPath;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Path to the user info endpoint.",
            example = "/protocol/openid-connect/userinfo"
    )
    @JsonProperty("user_info_path")
    public String userInfoPath;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Path to the token endpoint.",
            example = "/protocol/openid-connect/token"
    )
    @JsonProperty("token_path")
    public String tokenPath;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "URL of the OIDC server (e.g., Keycloak realm URL).",
            example = "http://localhost:58080/realms/quarkus"
    )
    @JsonProperty("auth_server_url")
    public String authServerUrl;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The name of the claim in the access token that uniquely identifies the user (e.g., 'sub', 'voperson_id').",
            example = "voperson_id"
    )
    @JsonProperty("user_id_token_claim")
    public String userIdTokenClaim;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The name of the claim in the access token that uniquely identifies the service (e.g., 'client_id').",
            example = "client_id"
    )
    @JsonProperty("service_id_token_claim")
    public String serviceIdTokenClaim;

}
