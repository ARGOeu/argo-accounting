package org.accounting.system.validators;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vavr.control.Try;
import org.accounting.system.constraints.AccessInstallation;
import org.accounting.system.enums.Collection;
import org.accounting.system.enums.Operation;
import org.accounting.system.exceptions.CustomValidationException;
import org.accounting.system.repositories.installation.InstallationRepository;
import org.accounting.system.repositories.project.ProjectRepository;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.accounting.system.util.Utility;
import org.bson.types.ObjectId;

import javax.enterprise.inject.spi.CDI;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * This {@link AccessInstallationValidator} defines the logic to validate the {@link AccessInstallation}.
 * Essentially, it checks if a client has access to a specific Project
 * If not, it throws a {@link io.quarkus.security.ForbiddenException} with http status 403.
 */
public class AccessInstallationValidator implements ConstraintValidator<AccessInstallation, String> {

    private String message;

    private Collection collection;

    private Operation operation;

    @Override
    public void initialize(AccessInstallation constraintAnnotation) {
        this.message = constraintAnnotation.message();
        this.collection = constraintAnnotation.collection();
        this.operation = constraintAnnotation.operation();
    }

    @Override
    public boolean isValid(String installationId, ConstraintValidatorContext constraintValidatorContext) {

        var installationRepository = CDI.current().select(InstallationRepository.class).get();

        var utility = CDI.current().select(Utility.class).get();

        utility.setAccessToken();


        Try
                .run(()->installationRepository.findByIdOptional(new ObjectId(installationId)).orElseThrow(()->new CustomValidationException("There is no Installation with the following id: "+installationId, HttpResponseStatus.NOT_FOUND)))
                .getOrElseThrow(()->new CustomValidationException("There is no Installation with the following id: "+installationId, HttpResponseStatus.NOT_FOUND));

        var installation = installationRepository.findById(new ObjectId(installationId));

        var projectRepository = CDI.current().select(ProjectRepository.class).get();

        var providerRepository = CDI.current().select(ProviderRepository.class).get();

        Boolean access = Try
                .of(()->projectRepository.accessibility(installation.getProject(), collection, operation))
                .mapTry(projectAccess-> {

                    if(!projectAccess){
                        return providerRepository.accessibility(installation.getProject(), installation.getOrganisation(), collection, operation);
                    }

                    return projectAccess;
                })
                .mapTry(providerAccess->{

                    if(!providerAccess){
                        return installationRepository.accessibility(installation.getProject(), installation.getOrganisation(), installationId, collection, operation);
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