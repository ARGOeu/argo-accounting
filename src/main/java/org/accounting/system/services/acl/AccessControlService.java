package org.accounting.system.services.acl;

import org.accounting.system.repositories.acl.AccessControlRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class AccessControlService {

    @Inject
    AccessControlRepository accessControlRepository;
}
