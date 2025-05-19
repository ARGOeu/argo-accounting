package org.accounting.system.validators;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vavr.control.Try;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.accounting.system.beans.RequestUserContext;
import org.accounting.system.constraints.AccessProject;
import org.accounting.system.enums.Collection;
import org.accounting.system.enums.Operation;
import org.accounting.system.exceptions.CustomValidationException;
import org.accounting.system.repositories.client.ClientRepository;
import org.accounting.system.repositories.project.ProjectRepository;

/**
 * This {@link AccessProjectValidator} defines the logic to validate the {@link AccessProject}.
 * Essentially, it checks if a client has access to a specific Project
 * If not, it throws a {@link io.quarkus.security.ForbiddenException} with http status 403.
 */
public class AccessProjectValidator implements ConstraintValidator<AccessProject, String> {

    private String message;

    private boolean checkIfExists;

    private Collection collection;

    private Operation operation;

    @Override
    public void initialize(AccessProject constraintAnnotation) {
        this.message = constraintAnnotation.message();
        this.checkIfExists = constraintAnnotation.checkIfExists();
        this.collection = constraintAnnotation.collection();
        this.operation = constraintAnnotation.operation();
    }

    @Override
    public boolean isValid(String project, ConstraintValidatorContext context) {

        var projectRepository = CDI.current().select(ProjectRepository.class).get();

        if(checkIfExists){
            Try
                    .run(()->projectRepository.findByIdOptional(project).orElseThrow(()->new CustomValidationException("There is no Project with the following id: "+project, HttpResponseStatus.NOT_FOUND)))
                    .getOrElseThrow(()->new CustomValidationException("There is no Project with the following id: "+project, HttpResponseStatus.NOT_FOUND));
        }

        var clientRepository = CDI.current().select(ClientRepository.class).get();

        var requestUserContext = CDI.current().select(RequestUserContext.class).get();


        if(clientRepository.isSystemAdmin(requestUserContext.getId())){

            return true;
        }

        var access =  projectRepository.accessibility(project, collection, operation);

        if(!access){
            throw new CustomValidationException(message, HttpResponseStatus.FORBIDDEN);
        } else {
            return true;
        }
    }
}