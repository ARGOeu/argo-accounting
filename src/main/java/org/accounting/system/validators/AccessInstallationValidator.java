package org.accounting.system.validators;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vavr.control.Try;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.accounting.system.constraints.AccessInstallation;
import org.accounting.system.entities.HierarchicalRelation;
import org.accounting.system.exceptions.CustomValidationException;
import org.accounting.system.repositories.HierarchicalRelationRepository;
import org.accounting.system.services.EntitlementService;

import java.util.List;
import java.util.regex.Pattern;

/**
 * This {@link AccessInstallationValidator} defines the logic to validate the {@link AccessInstallation}.
 * Essentially, it checks if a client has access to a specific Project
 * If not, it throws a {@link io.quarkus.security.ForbiddenException} with http status 403.
 */
public class AccessInstallationValidator implements ConstraintValidator<AccessInstallation, String> {

    private String message;

    private String[] roles;
    @Override
    public void initialize(AccessInstallation constraintAnnotation) {
        this.message = constraintAnnotation.message();
        this.roles = constraintAnnotation.roles();
    }

    @Override
    public boolean isValid(String installationId, ConstraintValidatorContext constraintValidatorContext) {

        var hierarchicalRelationRepository = CDI.current().select(HierarchicalRelationRepository.class).get();

        var hierarchicalRelationOptional = hierarchicalRelationRepository.find("externalId", installationId).firstResultOptional();

        var hierarchicalRelation = hierarchicalRelationOptional.orElseThrow(()-> new CustomValidationException("There is no Installation with the following id: "+installationId, HttpResponseStatus.NOT_FOUND));

        var ids = hierarchicalRelation.id.split(Pattern.quote(HierarchicalRelation.PATH_SEPARATOR));

        var entitlementService = CDI.current().select(EntitlementService.class).get();

        if(entitlementService.hasAccess("accounting", "admin", List.of())){

            return true;
        }

        Boolean access = Try
                .of(()->{

                    var enter = false;

                    for(String role:roles) {

                        if (entitlementService.hasAccess("accounting", role, List.of(ids[0]))) {

                            enter = true;
                        }
                    }

                    return enter;
                })
                .mapTry(projectAccess-> {

                    if(!projectAccess){

                        var enter = false;

                        for(String role:roles) {

                            if (entitlementService.hasAccess("accounting", role, List.of(ids[0], ids[1])) || entitlementService.hasAccess("accounting", role, List.of("roles", "provider", ids[1]))) {

                                enter = true;
                            }
                        }

                        return enter;
                    }

                    return projectAccess;
                })
                .mapTry(providerAccess->{

                    if(!providerAccess){

                        var enter = false;

                        for(String role:roles) {

                            if (entitlementService.hasAccess("accounting", role, List.of(ids[0], ids[1], installationId))) {

                                enter = true;
                            }
                        }

                        return enter;
                    }

                    return providerAccess;

                })
                .mapTry(installionAccess-> installionAccess).get();

        if(!access){
            throw new CustomValidationException(message, HttpResponseStatus.FORBIDDEN);
        } else {
            return access;
        }
    }
}