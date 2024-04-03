package org.accounting.system.interceptors;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.accounting.system.interceptors.annotations.SystemAdmin;
import org.accounting.system.repositories.client.ClientRepository;
import org.accounting.system.util.Utility;

@SystemAdmin
@Interceptor
@Priority(3000)
/**
 * This interceptor checks whether the client is a system admin. If so, it can execute the action that has the {@link SystemAdmin SystemAdmin} annotation.
 * Otherwise, a ForbiddenException exception is thrown.
 */
public class SystemAdminInterceptor {

    @Inject
    ClientRepository clientRepository;

    @Inject
    Utility utility;


    @AroundInvoke
    Object check(InvocationContext context) throws Exception {

        return hasAccess(context);
    }

    private Object hasAccess(InvocationContext context) throws Exception {

        clientRepository.isSystemAdmin(utility.getClientVopersonId());

        return context.proceed();
    }
}
