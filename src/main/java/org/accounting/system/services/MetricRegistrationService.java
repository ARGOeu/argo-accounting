package org.accounting.system.services;

import org.accounting.system.dtos.MetricRegistrationDtoRequest;
import org.accounting.system.dtos.MetricRegistrationDtoResponse;
import org.accounting.system.dtos.UpdateMetricRegistrationDtoRequest;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.mappers.MetricRegistrationMapper;
import org.accounting.system.repositories.MetricRegistrationRepository;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MetricRegistrationService {

    @Inject
    private MetricRegistrationRepository metricRegistrationRepository;

    public MetricRegistrationService(MetricRegistrationRepository metricRegistrationRepository) {
        this.metricRegistrationRepository = metricRegistrationRepository;
    }

    public MetricRegistrationDtoResponse save(MetricRegistrationDtoRequest request) {

        var metricRegistration = MetricRegistrationMapper.INSTANCE.requestToMetricRegistration(request);

        metricRegistrationRepository.persist(metricRegistration);

        return MetricRegistrationMapper.INSTANCE.metricRegistrationToResponse(metricRegistration);
    }

    public Optional<MetricRegistrationDtoResponse> fetchMetricRegistration(String id){

        var optionalMetricRegistration = metricRegistrationRepository.findByIdOptional(new ObjectId(id));

        return optionalMetricRegistration.map(MetricRegistrationMapper.INSTANCE::metricRegistrationToResponse).stream().findAny();
    }

    public List<MetricRegistrationDtoResponse> fetchAllMetricRegistrations(){

        var list = metricRegistrationRepository.findAll().list();

        return MetricRegistrationMapper.INSTANCE.metricRegistrationsToResponse(list);
    }

    public MetricRegistrationDtoResponse update(String id, UpdateMetricRegistrationDtoRequest request){

        var optionalMetricRegistration = metricRegistrationRepository.findByIdOptional(new ObjectId(id));

        var metricRegistration = optionalMetricRegistration.orElseThrow(()->new NotFoundException("The Metric Registration has not been found."));

        MetricRegistrationMapper.INSTANCE.updateMetricRegistrationFromDto(request, metricRegistration);

        metricRegistrationRepository.update(metricRegistration);

        return MetricRegistrationMapper.INSTANCE.metricRegistrationToResponse(metricRegistration);
    }

    /**
     *Τwo Metric Registrations are considered similar when having the same unit type and name.
     *
     * @param unitType Unit Type of the Virtual Access Metric
     * @param name The name of the Virtual Access Metric
     */
    public void exist(String unitType, String name){

        metricRegistrationRepository.exist(unitType, name)
                .ifPresent(metricRegistration -> {throw new ConflictException("There is a Metric Registration with unit type "+metricRegistration.getUnitType()+" and name "+metricRegistration.getMetricName()+". Its id is "+metricRegistration.getId().toString());});
    }

}