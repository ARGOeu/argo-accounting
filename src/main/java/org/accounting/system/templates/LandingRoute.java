package org.accounting.system.templates;

import io.quarkus.qute.Template;
import io.quarkus.vertx.web.Route;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class LandingRoute {

    @Inject
    Template index;

    @ConfigProperty(name = "api.accounting.aai.proxy.client.url")
    String aaiProxyClientUrl;

    @Route(path = "/", methods = Route.HttpMethod.GET)
    public void landing(RoutingContext rc) {
        rc
                .response()
                .putHeader("content-type", "text/html")
                .end(index.data("aai_proxy_client_url", aaiProxyClientUrl).render());
    }
}