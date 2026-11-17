package org.accounting.system.endpoints;

import org.grnet.endpoint.scanner.runtime.ApiResource;

public enum InstallationResource implements ApiResource {
    INSTALLATION;

    @Override
    public String resourceName() {
        return "Installation";
    }
}
