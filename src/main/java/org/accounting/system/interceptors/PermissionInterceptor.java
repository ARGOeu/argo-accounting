package org.accounting.system.interceptors;

import io.quarkus.arc.ArcInvocationContext;
import io.quarkus.oidc.TokenIntrospection;
import org.accounting.system.beans.RequestInformation;
import org.accounting.system.enums.AccessType;
import org.accounting.system.interceptors.annotations.Permission;
import org.accounting.system.services.authorization.RoleService;
import org.accounting.system.util.DisabledAuthController;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.json.JsonArray;
import javax.json.JsonString;
import javax.ws.rs.ForbiddenException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Permission
@Interceptor
@Priority(3000)
/**
 * This interceptor checks whether the service/user has the appropriate permission to create, update, delete or read an entity.
 * It collects the {@link org.accounting.system.enums.Operation} and the {@link org.accounting.system.enums.Collection} from the
 * {@link Permission} as well as the service/user role from {@link TokenIntrospection}, which contains the metadata of an access token.
 * Combining this information, we execute a query to Role collection to inspect if this role can execute that operation to that specific collection.
 */
public class PermissionInterceptor {

    @Inject
    TokenIntrospection tokenIntrospection;

    @Inject
    RoleService roleService;

    @Inject
    DisabledAuthController authorizationController;

    @Inject
    RequestInformation requestInformation;

    @ConfigProperty(name = "key.to.retrieve.roles.from.access.token")
    String key;

    @AroundInvoke
    Object check(InvocationContext context) throws Exception {

        if(!authorizationController.isAuthorizationEnabled()){
            requestInformation.setAccessType(AccessType.ALWAYS);
            return context.proceed();
        } else {
            return hasAccess(context);
        }
    }

    private Object hasAccess(InvocationContext context) throws Exception {

        Permission permission = Stream
                .of(context.getContextData().get(ArcInvocationContext.KEY_INTERCEPTOR_BINDINGS))
                .map(annotations-> (Set<Annotation>) annotations)
                .flatMap(java.util.Collection::stream)
                .filter(annotation -> annotation.annotationType().equals(Permission.class))
                .map(annotation -> (Permission) annotation)
                .findFirst()
                .get();

        JsonArray jsonArray = tokenIntrospection.getArray(key);

        if(Objects.isNull(jsonArray)){
            throw new ForbiddenException("The authenticated user/service is not permitted to perform the requested operation.");
        }

        List<String> providedRoles = jsonArray
                .stream()
                .map(jsonValue -> ((JsonString) jsonValue).getString())
                .collect(Collectors.toList());

        // sub -> Usually a machine-readable identifier of the resource owner who authorized this token
        requestInformation.setSubjectOfToken(tokenIntrospection.getJsonObject().getString("sub"));

        var access = roleService.hasAccess(providedRoles, permission.collection(), permission.operation());

        if(!access){
            throw new ForbiddenException("The authenticated user/service is not permitted to perform the requested operation.");
        }

        return context.proceed();
    }
}
