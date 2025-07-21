package org.accounting.system;

import io.quarkus.test.Mock;
import jakarta.enterprise.context.RequestScoped;
import org.accounting.system.beans.RequestUserContext;

@Mock
@RequestScoped
public class MockRequestUserContext extends RequestUserContext {

    @Override
    public String getIssuer() {

        return "http://localhost:58080/realms/quarkus";
    }
}
