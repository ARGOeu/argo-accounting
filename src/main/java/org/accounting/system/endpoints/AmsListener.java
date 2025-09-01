package org.accounting.system.endpoints;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.ams.AmsMessage;
import org.accounting.system.services.AmsService;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;

import java.util.Arrays;
import java.util.stream.Collectors;

@Path("/")
public class AmsListener {

    @Inject
    AmsService amsService;

    @ConfigProperty(name = "api.accounting.ams.verification.hash")
    String hash;

    @ConfigProperty(name = "api.accounting.ams.authorization.headers", defaultValue = "")
    String amsAuthorizationHeaders;

    @Operation(hidden = true)
    @POST
    @Path("ams-listener")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response amsListener(AmsMessage message, @HeaderParam("Authorization") String authorization){

        var headers = Arrays.stream(amsAuthorizationHeaders.split(",")).map(String::trim).collect(Collectors.toList());

        if(StringUtils.isEmpty(authorization) || !headers.contains(authorization)){

            throw new ForbiddenException("You cannot perform this operation");
        }

        amsService.consumeAmsMessage(message);

        var response = new InformativeResponse();
        response.code = 200;
        response.message = "AMS message has been successfully consumed";

        return Response.ok().entity(response).build();
    }
}
