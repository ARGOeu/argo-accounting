package org.accounting.system.interceptors.annotations;

import org.accounting.system.dtos.acl.role.RoleAccessControlRequestDto;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@InterceptorBinding
@Target({METHOD, TYPE})
@Retention(RUNTIME)

/**
 * This annotation should only be applied to {@link org.accounting.system.services.acl.RoleAccessControlService#grantPermission(String, RoleAccessControlRequestDto, String...)}  method}.
 * If the {@link RoleAccessControlRequestDto Access Control Entry} contains the role parameter then the roles encapsulated to additionalRoles parameter
 * are going to be granted as well.
 */
public @interface AdditionalPermissions {

    /**
     * The role to be checked whether is part of {@link RoleAccessControlRequestDto Access Control Entry}
     */
    @Nonbinding String role() default "";

    /**
     * Additional roles that are going to be granted
     */
    @Nonbinding String[] additionalRoles() default {};

}
