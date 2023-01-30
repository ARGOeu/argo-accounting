package org.accounting.system.endpoints;

import io.quarkus.security.Authenticated;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.enums.EnumResponseDto;
import org.accounting.system.enums.Operator;
import org.accounting.system.mappers.EnumMapper;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeIn;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.List;

@Path("/operators")
@Authenticated
@SecurityScheme(securitySchemeName = "Authentication",
        description = "JWT token",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER)

public class OperatorEndpoint {


    @Tag(name = "Operator")
    @org.eclipse.microprofile.openapi.annotations.Operation(
            summary = "Retrieve the operators that will be used to syntax an equation",
            description = "Retrieve the operators that will be used to syntax an equation")
    @APIResponse(
            responseCode = "200",
            description = "Operators have been successfully registered.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = EnumResponseDto.class)))
    @APIResponse(
            responseCode = "401",
            description = "Client has not been authenticated.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "403",
            description = "The authenticated client is not permitted to perform the requested operation.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Errors.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @SecurityRequirement(name = "Authentication")


    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public List<EnumResponseDto> getOperators() {

        var operators = Arrays.asList(Operator.values());
        return EnumMapper.INSTANCE.operatorsToResponse(operators);
    }

}