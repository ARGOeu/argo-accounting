package org.accounting.system.interceptors;

import io.quarkus.oidc.TokenIntrospection;
import org.accounting.system.beans.RequestInformation;
import org.accounting.system.enums.AccessType;
import org.accounting.system.interceptors.annotations.AccessPermission;
import org.accounting.system.interceptors.annotations.AccessPermissions;
import org.accounting.system.interceptors.annotations.AccessPermissionsUtil;
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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@AccessPermissions
@Interceptor
@Priority(3000)
/**
 * This interceptor checks whether the client has the appropriate permission to create, update, delete or read an entity.
 * It collects the {@link org.accounting.system.enums.Operation} and the {@link org.accounting.system.enums.Collection} from the
 * {@link AccessPermissions} as well as the client role from {@link TokenIntrospection}, which contains the metadata of an access token.
 * Combining this information, we execute a query to Role collection to inspect if this role can execute that operation to that specific collection.
 */
public class AccessPermissionsInterceptor {

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

        List<String> providedRoles = null;

        Object object = tokenIntrospection.getJsonObject().get(key);

        if(object instanceof JsonString){
            providedRoles = List.of(tokenIntrospection.getString(key));
        } else {
            JsonArray jsonArray = tokenIntrospection.getArray(key);
            providedRoles = jsonArray
                    .stream()
                    .map(jsonValue -> ((JsonString) jsonValue).getString())
                    .collect(Collectors.toList());
        }

        if(Objects.isNull(providedRoles)){
            throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
        }

        // sub -> Usually a machine-readable identifier of the resource owner who authorized this token
        requestInformation.setSubjectOfToken(tokenIntrospection.getJsonObject().getString("sub"));

        var  accessPermissionsUtilList = new ArrayList<AccessPermissionsUtil>();

        Method method = context.getMethod();
        if (method.isAnnotationPresent(AccessPermissions.class)) {
            AccessPermission[] ourAnnotations = method.getAnnotation(AccessPermissions.class).value();
            for (AccessPermission annotation : ourAnnotations) {
               roleService.hasAccess(providedRoles, annotation.collection(), annotation.operation(), annotation.precedence(), accessPermissionsUtilList);
            }
        }

        if(accessPermissionsUtilList.isEmpty()){
            throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
        }

        Collections.sort(accessPermissionsUtilList, Comparator.comparingInt(AccessPermissionsUtil::getPermissionPrecedence));

        requestInformation.setAccessPermissions(accessPermissionsUtilList);

        return context.proceed();
    }
}
