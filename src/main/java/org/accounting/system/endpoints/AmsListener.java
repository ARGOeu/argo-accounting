package org.accounting.system.endpoints;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.ams.AmsMessage;
import org.accounting.system.services.AmsService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/")
public class AmsListener {

    @Inject
    AmsService amsService;

    @ConfigProperty(name = "api.accounting.ams.verification.hash")
    String hash;

    @Operation(hidden = true)
    @POST
    @Path("ams-listener")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response amsListener(AmsMessage message){

        amsService.consumeAmsMessage(message);

        var response = new InformativeResponse();
        response.code = 200;
        response.message = "AMS message has been successfully consumed";

        return Response.ok().entity(response).build();
    }

    @Operation(hidden = true)
    @GET
    @Path("ams_verification_hash")
    @Produces(value = MediaType.TEXT_PLAIN)
    public Response amsVerificationHash(){

        return Response.ok().entity(hash).build();
    }
}
