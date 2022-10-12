package org.accounting.system.repositories.provider;

import org.accounting.system.entities.acl.RoleAccessControl;
import org.accounting.system.entities.provider.Provider;
import org.accounting.system.repositories.modulators.AccessControlModulator;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProviderAccessControlRepository extends AccessControlModulator<Provider, String, RoleAccessControl> {

}
