package org.accounting.system.interceptors.annotations;

import jakarta.enterprise.util.Nonbinding;
import jakarta.interceptor.InterceptorBinding;
import org.accounting.system.enums.Collection;
import org.accounting.system.enums.Operation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@InterceptorBinding
@Target({METHOD, TYPE})
@Retention(RUNTIME)
public @interface AccessPermission {

    @Nonbinding Collection collection() default Collection.MetricDefinition;
    @Nonbinding Operation operation() default Operation.CREATE;
}
