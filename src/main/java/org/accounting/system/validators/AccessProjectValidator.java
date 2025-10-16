package org.accounting.system.validators;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vavr.control.Try;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.accounting.system.beans.RequestUserContext;
import org.accounting.system.constraints.AccessProject;
import org.accounting.system.exceptions.CustomValidationException;
import org.accounting.system.repositories.project.ProjectRepository;
import org.accounting.system.services.EntitlementService;

import java.util.List;

/**
 * This {@link AccessProjectValidator} defines the logic to validate the {@link AccessProject}.
 * Essentially, it checks if a client has access to a specific Project
 * If not, it throws a {@link io.quarkus.security.ForbiddenException} with http status 403.
 */
public class AccessProjectValidator implements ConstraintValidator<AccessProject, String> {

    private String message;

    private boolean checkIfExists;

    private String[] roles;
    @Override
    public void initialize(AccessProject constraintAnnotation) {
        this.message = constraintAnnotation.message();
        this.checkIfExists = constraintAnnotation.checkIfExists();
        this.roles = constraintAnnotation.roles();
    }

    @Override
    public boolean isValid(String project, ConstraintValidatorContext context) {

        var projectRepository = CDI.current().select(ProjectRepository.class).get();

        var entitlementService = CDI.current().select(EntitlementService.class).get();

        var requestUserContext = CDI.current().select(RequestUserContext.class).get();

        if(checkIfExists){
            Try
                    .run(()->projectRepository.findByIdOptional(project).orElseThrow(()->new CustomValidationException("There is no Project with the following id: "+project, HttpResponseStatus.NOT_FOUND)))
                    .getOrElseThrow(()->new CustomValidationException("There is no Project with the following id: "+project, HttpResponseStatus.NOT_FOUND));
        }

        if(requestUserContext.getOidcTenantConfig().isEmpty() && entitlementService.hasAccess(requestUserContext.getParent(), "admin", List.of())){

            return true;
        }

        for(String role:roles){

            if(entitlementService.hasAccess(requestUserContext.getParent(), role, List.of(project))){

                return true;
            }
        }

        throw new CustomValidationException(message, HttpResponseStatus.FORBIDDEN);
    }
}