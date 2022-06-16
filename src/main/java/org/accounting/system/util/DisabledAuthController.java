package org.accounting.system.util;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DisabledAuthController {
    @ConfigProperty(name = "disable.authorization", defaultValue = "false")
    boolean disableAuthorization;

    public boolean isAuthorizationEnabled() {
        return !disableAuthorization;
    }
}

//@Alternative
//@Priority(Interceptor.Priority.LIBRARY_AFTER)
//@ApplicationScoped
//public class DisabledAuthController extends AuthorizationController {
//    @ConfigProperty(name = "disable.authorization", defaultValue = "false")
//    boolean disableAuthorization;
//
//    @Override
//    public boolean isAuthorizationEnabled() {
//        return !disableAuthorization;
//    }
//}