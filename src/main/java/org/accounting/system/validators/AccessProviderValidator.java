package org.accounting.system.validators;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vavr.control.Try;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;
import org.accounting.system.constraints.AccessProvider;
import org.accounting.system.dtos.installation.InstallationRequestDto;
import org.accounting.system.enums.Collection;
import org.accounting.system.enums.Operation;
import org.accounting.system.exceptions.CustomValidationException;
import org.accounting.system.repositories.metricdefinition.MetricDefinitionRepository;
import org.accounting.system.repositories.project.ProjectRepository;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.accounting.system.util.Utility;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;

import java.util.Objects;

/**
 * This {@link AccessProviderValidator} defines the logic to validate the {@link AccessProvider}.
 * Essentially, it checks if a client has access to a specific Provider of a Project
 * If not, it throws a {@link io.quarkus.security.ForbiddenException} with http status 403.
 */
@SupportedValidationTarget(ValidationTarget.PARAMETERS)
public class AccessProviderValidator implements ConstraintValidator<AccessProvider, Object[]> {

    private String message;

    private Collection collection;

    private Operation operation;

    @Override
    public void initialize(AccessProvider constraintAnnotation) {
        this.message = constraintAnnotation.message();
        this.collection = constraintAnnotation.collection();
        this.operation = constraintAnnotation.operation();
    }

    @Override
    public boolean isValid(Object[] objects, ConstraintValidatorContext constraintValidatorContext) {

        if(objects[0] instanceof String){

            return access((String) objects[0], (String) objects[1]);

        } else if (objects[0] instanceof InstallationRequestDto) {

            InstallationRequestDto request = (InstallationRequestDto) objects[0];

            validRequest(request);

            return access(request.project, request.organisation);
        } else if (objects[0] == null) {
            throw new CustomValidationException("The request body is empty.", HttpResponseStatus.BAD_REQUEST);
        } else {
            return false;
        }
    }

    private boolean access(String project, String provider){

        var projectRepository = CDI.current().select(ProjectRepository.class).get();

        var providerRepository = CDI.current().select(ProviderRepository.class).get();

        var utility = CDI.current().select(Utility.class).get();

        utility.setAccessToken();

        Try
                .run(()->projectRepository.findByIdOptional(project).orElseThrow(()->new CustomValidationException("There is no Project with the following id: "+project, HttpResponseStatus.NOT_FOUND)))
                .getOrElseThrow(()->new CustomValidationException("There is no Project with the following id: "+project, HttpResponseStatus.NOT_FOUND));

        Try
                .run(()->providerRepository.findByIdOptional(provider).orElseThrow(()->new CustomValidationException("There is no Provider with the following id: "+provider, HttpResponseStatus.NOT_FOUND)))
                .getOrElseThrow(()->new CustomValidationException("There is no Provider with the following id: "+provider, HttpResponseStatus.NOT_FOUND));

        providerRepository.findByIdOptional(provider).orElseThrow(()->new CustomValidationException("There is no Provider with the following id: "+provider, HttpResponseStatus.NOT_FOUND));

        Boolean access = Try
                .of(()->projectRepository.accessibility(project, collection, operation))
                .mapTry(projectAccess-> {

                    if(!projectAccess){
                        return providerRepository.accessibility(project, provider, collection, operation);
                    }

                    return projectAccess;
                })
                .mapTry(providerAccess-> providerAccess).get();

        if(!access){
            throw new CustomValidationException(message, HttpResponseStatus.FORBIDDEN);
        } else {
            return access;
        }
    }

    public void validRequest(InstallationRequestDto request){

        if(Objects.isNull(request)){

            throw new CustomValidationException("The request body is empty.", HttpResponseStatus.BAD_REQUEST);
        }

        if(StringUtils.isEmpty(request.project)){

            throw new CustomValidationException("project may not be empty.", HttpResponseStatus.BAD_REQUEST);
        }

        if(StringUtils.isEmpty(request.organisation)){

            throw new CustomValidationException("organisation may not be empty.", HttpResponseStatus.BAD_REQUEST);
        }

        if(StringUtils.isEmpty(request.organisation)){

            throw new CustomValidationException("organisation may not be empty.", HttpResponseStatus.BAD_REQUEST);
        }

    
        if(StringUtils.isEmpty(request.installation)){

            throw new CustomValidationException("installation may not be empty.", HttpResponseStatus.BAD_REQUEST);
        }

        if(StringUtils.isEmpty(request.unitOfAccess)){

            throw new CustomValidationException("unit_of_access may not be empty.", HttpResponseStatus.BAD_REQUEST);
        }

        var metricDefinitionRepository = CDI.current().select(MetricDefinitionRepository.class).get();


        Try
                .run(()->metricDefinitionRepository.findByIdOptional(new ObjectId(request.unitOfAccess)).orElseThrow(()->new CustomValidationException("There is no Metric Definition with the following id "+request.unitOfAccess, HttpResponseStatus.NOT_FOUND)))
                .getOrElseThrow(()->new CustomValidationException("There is no Metric Definition with the following id: "+request.unitOfAccess, HttpResponseStatus.NOT_FOUND));
    }
}