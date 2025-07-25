package org.accounting.system.validators;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vavr.control.Try;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.accounting.system.beans.RequestUserContext;
import org.accounting.system.constraints.AccessInstallation;
import org.accounting.system.entities.HierarchicalRelation;
import org.accounting.system.enums.Collection;
import org.accounting.system.enums.Operation;
import org.accounting.system.exceptions.CustomValidationException;
import org.accounting.system.repositories.HierarchicalRelationRepository;
import org.accounting.system.repositories.client.ClientRepository;
import org.accounting.system.repositories.installation.InstallationRepository;
import org.accounting.system.repositories.project.ProjectRepository;
import org.accounting.system.repositories.provider.ProviderRepository;

import java.util.regex.Pattern;

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

        var hierarchicalRelationRepository = CDI.current().select(HierarchicalRelationRepository.class).get();

        var hierarchicalRelationOptional = hierarchicalRelationRepository.find("externalId", installationId).firstResultOptional();

        var hierarchicalRelation = hierarchicalRelationOptional.orElseThrow(()-> new CustomValidationException("There is no Installation with the following id: "+installationId, HttpResponseStatus.NOT_FOUND));

        var ids = hierarchicalRelation.id.split(Pattern.quote(HierarchicalRelation.PATH_SEPARATOR));

        var projectRepository = CDI.current().select(ProjectRepository.class).get();

        var providerRepository = CDI.current().select(ProviderRepository.class).get();

        var clientRepository = CDI.current().select(ClientRepository.class).get();

        var requestUserContext = CDI.current().select(RequestUserContext.class).get();

        if(clientRepository.isSystemAdmin(requestUserContext.getId())){

            return true;
        }

        Boolean access = Try
                .of(()->projectRepository.accessibility(ids[0], collection, operation))
                .mapTry(projectAccess-> {

                    if(!projectAccess){
                        return providerRepository.accessibility(ids[0], ids[1], collection, operation);
                    }

                    return projectAccess;
                })
                .mapTry(providerAccess->{

                    if(!providerAccess){
                        return installationRepository.accessibility(ids[0], ids[1], installationId, collection, operation);
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