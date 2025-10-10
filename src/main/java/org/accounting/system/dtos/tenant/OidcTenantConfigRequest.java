package org.accounting.system.dtos.tenant;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="OidcTenantConfig", description="An object represents a request for creating an Oidc Tenant Configuration.")
public class OidcTenantConfigRequest {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Issuer URI for the tokens (often the same as `authServerUrl`).",
            required = true,
            example = "http://localhost:58080/realms/quarkus"
    )
    @JsonProperty("issuer")
    @NotEmpty(message = "issuer may not be empty.")
    public String issuer;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Unique identifier for the tenant.",
            required = true,
            example = "default"
    )
    @JsonProperty("tenant_id")
    @NotEmpty(message = "tenant_id may not be empty.")
    public String tenantId;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Client ID registered with the identity provider.",
            required = true,
            example = "backend-service"
    )
    @JsonProperty("client_id")
    @NotEmpty(message = "client_id may not be empty.")
    public String clientId;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Client secret for authenticating the client.",
            required = true,
            example = "secret"
    )
    @JsonProperty("secret")
    @NotEmpty(message = "secret may not be empty.")
    public String secret;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Path to the authorization endpoint.",
            required = true,
            example = "/protocol/openid-connect/auth"
    )
    @JsonProperty("authorization_path")
    @NotEmpty(message = "authorization_path may not be empty.")
    public String authorizationPath;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Path to the token introspection endpoint.",
            required = true,
            example = "/protocol/openid-connect/token/introspect"
    )
    @JsonProperty("introspection_path")
    @NotEmpty(message = "introspection_path may not be empty.")
    public String introspectionPath;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Path to the user info endpoint.",
            required = true,
            example = "/protocol/openid-connect/userinfo"
    )
    @JsonProperty("user_info_path")
    @NotEmpty(message = "user_info_path may not be empty.")
    public String userInfoPath;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Path to the token endpoint.",
            required = true,
            example = "/protocol/openid-connect/token"
    )
    @JsonProperty("token_path")
    @NotEmpty(message = "token_path may not be empty.")
    public String tokenPath;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "URL of the OIDC server (e.g., Keycloak realm URL).",
            required = true,
            example = "http://localhost:58080/realms/quarkus"
    )
    @JsonProperty("auth_server_url")
    @NotEmpty(message = "auth_server_url may not be empty.")
    public String authServerUrl;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The name of the claim in the access token that uniquely identifies the user (e.g., 'sub', 'voperson_id').",
            required = true,
            example = "voperson_id"
    )
    @JsonProperty("user_id_token_claim")
    @NotEmpty(message = "user_id_token_claim may not be empty.")
    public String userIdTokenClaim;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The name of the claim in the access token that uniquely identifies the service (e.g., 'client_id').",
            required = true,
            example = "client_id"
    )
    @JsonProperty("service_id_token_claim")
    @NotEmpty(message = "service_id_token_claim may not be empty.")
    public String serviceIdTokenClaim;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The group management entitlement namespace.",
            required = true,
            example = "urn:geant:sandbox.eosc-beyond.eu:core:integration:group"
    )
    @JsonProperty("group_management_namespace")
    @NotEmpty(message = "group_management_namespace may not be empty.")
    public String groupManagementNamespace;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The name of the parent group management.",
            required = true,
            example = "accounting"
    )
    @JsonProperty("group_management_parent")
    @NotEmpty(message = "group_management_parent may not be empty.")
    public String groupManagementParent;
}
