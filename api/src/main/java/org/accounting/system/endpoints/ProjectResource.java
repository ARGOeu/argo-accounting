package org.accounting.system.endpoints;

import org.grnet.endpoint.scanner.runtime.ApiResource;

public enum ProjectResource implements ApiResource {

    PROJECT;

    @Override
    public String resourceName() {
        return "Project";
    }
}
