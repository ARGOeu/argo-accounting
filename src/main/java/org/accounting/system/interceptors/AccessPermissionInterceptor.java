package org.accounting.system.interceptors;

import io.quarkus.arc.ArcInvocationContext;
import io.quarkus.oidc.TokenIntrospection;
import org.accounting.system.interceptors.annotations.AccessPermission;
import org.accounting.system.services.authorization.RoleService;
import org.accounting.system.util.Utility;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.ws.rs.ForbiddenException;
import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

@AccessPermission
@Interceptor
@Priority(3000)
/**
 * This interceptor checks whether the client has the appropriate permission to create, update, delete or read an entity.
 * It collects the {@link org.accounting.system.enums.Operation} and the {@link org.accounting.system.enums.Collection} from the
 * {@link AccessPermission} as well as the client role from {@link TokenIntrospection}, which contains the metadata of an access token.
 * Combining this information, we execute a query to Role collection to inspect if this role can execute that operation to that specific collection.
 */
public class AccessPermissionInterceptor {

    @Inject
    RoleService roleService;

    @Inject
    Utility utility;

    @AroundInvoke
    Object check(InvocationContext context) throws Exception {

        return hasAccess(context);
    }

    private Object hasAccess(InvocationContext context) throws Exception {

        AccessPermission accessPermission = Stream
                .of(context.getContextData().get(ArcInvocationContext.KEY_INTERCEPTOR_BINDINGS))
                .map(annotations-> (Set<Annotation>) annotations)
                .flatMap(java.util.Collection::stream)
                .filter(annotation -> annotation.annotationType().equals(AccessPermission.class))
                .map(annotation -> (AccessPermission) annotation)
                .findFirst()
                .get();

        Set<String> providedRoles = utility.getRoles();

        if(Objects.isNull(providedRoles)){
            throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
        }

        var access = roleService.hasAccess(providedRoles, accessPermission.collection(), accessPermission.operation());

        if(!access){
            throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
        }

        return context.proceed();
    }
}
