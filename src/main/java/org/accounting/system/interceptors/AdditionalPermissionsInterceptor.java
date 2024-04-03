package org.accounting.system.interceptors;

import io.quarkus.arc.ArcInvocationContext;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.accounting.system.dtos.acl.role.RoleAccessControlRequestDto;
import org.accounting.system.interceptors.annotations.AdditionalPermissions;
import org.accounting.system.repositories.client.ClientAccessAlwaysRepository;
import org.accounting.system.services.acl.RoleAccessControlService;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AdditionalPermissions
@Interceptor
@Priority(3000)
public class AdditionalPermissionsInterceptor {

    @Inject
    ClientAccessAlwaysRepository clientAccessAlwaysRepository;

    @AroundInvoke
    Object check(InvocationContext context) throws Exception {

        var proceed = context.proceed();

        //Upon success granting permission method execution, we do the following:

        //First, we check if the AdditionalPermissions annotation has been applied to grantPermission method
        var allInterfaceMethods = RoleAccessControlService.class.getDeclaredMethods();

        var grantPermissionMethod = Arrays.stream(allInterfaceMethods)
                .filter(method -> method.getName().equals("grantPermission"))
                .findAny()
                .get();

        var methodIsBeingExecuted = context.getMethod();

        //If not, we proceed with the method execution. We essentially ignore the annotation.
        if(!methodIsBeingExecuted.getName().equals(grantPermissionMethod.getName())){

            return proceed;
        }

        //Second, we have to get the declared annotation parameters : role and additionalRoles
        AdditionalPermissions additionalPermissions = Stream
                .of(context.getContextData().get(ArcInvocationContext.KEY_INTERCEPTOR_BINDINGS))
                .map(annotations-> (Set<Annotation>) annotations)
                .flatMap(java.util.Collection::stream)
                .filter(annotation -> annotation.annotationType().equals(AdditionalPermissions.class))
                .map(annotation -> (AdditionalPermissions) annotation)
                .findFirst()
                .get();

        //Third, we check if the RoleAccessControlRequestDto contains the role declared to AdditionalPermissions annotation
        var ifSo = Arrays.stream(context.getParameters())
                .filter(object->object instanceof RoleAccessControlRequestDto)
                .map(object -> (RoleAccessControlRequestDto) object)
                .flatMap(acl->acl.roles.stream())
                .anyMatch(roleName->roleName.equals(additionalPermissions.role()));

        var objects = Arrays.stream(context.getParameters()).toArray();

        //If so, then we grant client the roles declared to AdditionalPermissions annotation

        if(ifSo){
            clientAccessAlwaysRepository.assignRolesToRegisteredClient((String) objects[0], Arrays.stream(additionalPermissions.additionalRoles()).collect(Collectors.toSet()));
        }

        return proceed;
    }
}