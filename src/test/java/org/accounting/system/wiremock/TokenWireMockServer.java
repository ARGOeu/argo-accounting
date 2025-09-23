package org.accounting.system.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.util.Collections;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class TokenWireMockServer implements QuarkusTestResourceLifecycleManager {

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {

        wireMockServer = new WireMockServer(3471);
        wireMockServer.start();

        wireMockServer.stubFor(post(urlEqualTo("/protocol/openid-connect/token"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBodyFile("json/Token.json")));

        return Collections.singletonMap("quarkus.rest-client.keycloak-token-client.url", wireMockServer.baseUrl());
    }

    @Override
    public void stop() {

        if (null != wireMockServer) {
            wireMockServer.stop();
        }
    }
}
