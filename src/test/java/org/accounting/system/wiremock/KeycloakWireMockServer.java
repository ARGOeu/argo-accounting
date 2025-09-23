package org.accounting.system.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.util.Collections;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

public class KeycloakWireMockServer implements QuarkusTestResourceLifecycleManager {

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {

        wireMockServer = new WireMockServer(3470);
        wireMockServer.start();

        wireMockServer.stubFor(get(urlEqualTo("/agm/account/group-admin/groups?search=accounting"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBodyFile("json/Groups.json")));

        wireMockServer.stubFor(post(urlPathMatching("/agm/account/group-admin/group/[^/]+/children"))
                .willReturn(aResponse()
                        .withStatus(200)));

        wireMockServer.stubFor(post(urlPathMatching("/agm/account/group-admin/group/.*"))
                .willReturn(aResponse()
                        .withStatus(200)));

        wireMockServer.stubFor(post(urlPathMatching("/group-admin/group/[^/]+/roles"))
                .withQueryParam("name", matching(".*"))
                .willReturn(aResponse()
                        .withStatus(200)));

        wireMockServer.stubFor(post(urlPathMatching("/agm/account/group-admin/group/[^/]+/configuration"))
                .willReturn(aResponse()
                        .withStatus(200)));

        return Collections.singletonMap("quarkus.rest-client.keycloak-group-client.url", wireMockServer.baseUrl());
    }

    @Override
    public void stop() {

        if (null != wireMockServer) {
            wireMockServer.stop();
        }
    }
}
