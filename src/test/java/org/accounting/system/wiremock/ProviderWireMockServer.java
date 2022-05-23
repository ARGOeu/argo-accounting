package org.accounting.system.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.util.Collections;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class ProviderWireMockServer implements QuarkusTestResourceLifecycleManager {

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {

        wireMockServer = new WireMockServer(3468);
        wireMockServer.start();

        wireMockServer.stubFor(get(urlEqualTo("/provider/all"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBodyFile("json/ProviderResponse.json")));

        wireMockServer.stubFor(get(urlEqualTo("/provider/all?quantity=237"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBodyFile("json/ProviderResponse.json")));

        return Collections.singletonMap("quarkus.rest-client.\"org.accounting.system.clients.ProviderClient\".url", wireMockServer.baseUrl());
    }

    @Override
    public void stop() {
        if (null != wireMockServer) {
            wireMockServer.stop();
        }
    }
}
