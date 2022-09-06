package org.accounting.system.templates;

import io.quarkus.qute.Template;
import io.quarkus.vertx.web.Route;
import io.vertx.ext.web.RoutingContext;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class LandingRoute {

    @Inject
    Template index;

    @ConfigProperty(name = "aai.proxy.client.url")
    String aaiProxyClientUrl;

    @Route(path = "/", methods = Route.HttpMethod.GET)
    public void landing(RoutingContext rc) {
        rc.response().end(index.data("aai_proxy_client_url", aaiProxyClientUrl).render());
    }
}