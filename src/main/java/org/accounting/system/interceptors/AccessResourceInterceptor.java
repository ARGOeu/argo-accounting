package org.accounting.system.interceptors;

import io.quarkus.arc.ArcInvocationContext;
import io.quarkus.oidc.TokenIntrospection;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.ForbiddenException;
import org.accounting.system.enums.ApiMessage;
import org.accounting.system.interceptors.annotations.AccessResource;
import org.accounting.system.services.EntitlementService;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@AccessResource
@Interceptor
@Priority(3000)
/**
 * This interceptor checks whether the client has the appropriate permission to create, update, delete or read an entity.
 * It collects the {@link org.accounting.system.enums.Operation} and the {@link org.accounting.system.enums.Collection} from the
 * {@link AccessResource} as well as the client role from {@link TokenIntrospection}, which contains the metadata of an access token.
 * Combining this information, we execute a query to Role collection to inspect if this role can execute that operation to that specific collection.
 */
public class AccessResourceInterceptor {

    @Inject
    EntitlementService entitlementService;


    @AroundInvoke
    Object check(InvocationContext context) throws Exception {

        return hasAccess(context);
    }

    private Object hasAccess(InvocationContext context) throws Exception {

        var accessResource = Stream
                .of(context.getContextData().get(ArcInvocationContext.KEY_INTERCEPTOR_BINDINGS))
                .map(annotations-> (Set<Annotation>) annotations)
                .flatMap(java.util.Collection::stream)
                .filter(annotation -> annotation.annotationType().equals(AccessResource.class))
                .map(annotation -> (AccessResource) annotation)
                .findFirst()
                .get();

        var roles = accessResource.roles();

        if(entitlementService.hasAccess("accounting", "admin", List.of())){

            return context.proceed();
        }

        for(String role:roles){

            if(entitlementService.hasAccess("accounting", role, List.of("operations", "resources"))){

                return context.proceed();
            }
        }

        throw new ForbiddenException(ApiMessage.UNAUTHORIZED_CLIENT.message);
    }
}
