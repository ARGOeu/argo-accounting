package org.accounting.system.health;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.accounting.system.clients.AAIProxyClient;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Readiness
@ApplicationScoped
public class AAIProxyHealthCheck implements HealthCheck {

    @Inject
    @RestClient
    AAIProxyClient AAIProxyClient;


    @Override
    public HealthCheckResponse call() {

        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("Authentication and Authorization Infrastructure health check");

        try {
            Response response = AAIProxyClient.configurations();

            if(response.getStatus() == Response.Status.OK.getStatusCode()){
                responseBuilder.up();
            } else {
                responseBuilder.down().withData("error", "Cannot access the Authentication and Authorization Infrastructure.");
            }

        } catch (Exception e){

            responseBuilder.down().withData("error", "Cannot access the Authentication and Authorization Infrastructure.");
        }

        return responseBuilder.build();
    }
}
