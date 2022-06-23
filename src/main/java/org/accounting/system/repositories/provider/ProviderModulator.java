package org.accounting.system.repositories.provider;

import org.accounting.system.dtos.provider.UpdateProviderRequestDto;
import org.accounting.system.entities.acl.RoleAccessControl;
import org.accounting.system.entities.projections.InstallationProjection;
import org.accounting.system.entities.projections.MetricProjection;
import org.accounting.system.entities.projections.ProjectionQuery;
import org.accounting.system.entities.provider.Provider;
import org.accounting.system.enums.Collection;
import org.accounting.system.enums.Operation;
import org.accounting.system.mappers.ProviderMapper;
import org.accounting.system.repositories.modulators.AbstractModulator;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import java.util.Objects;


public class ProviderModulator extends AbstractModulator<Provider, String, RoleAccessControl> {


    @Inject
    ProviderAccessEntityRepository providerAccessEntityRepository;

    @Inject
    ProviderAccessAlwaysRepository providerAccessAlwaysRepository;

    /**
     * This method is responsible for updating a part or all attributes of existing Provider.
     *
     * @param id The Provider to be updated.
     * @param request The Provider attributes to be updated.
     * @return The updated Provider.
     * @throws ForbiddenException If Provider derives from EOSC-Portal.
     */
    public Provider updateEntity(String id, UpdateProviderRequestDto request) {

        Provider entity = findById(id);

        // if Provider's creator id is null then it derives from EOSC-Portal
        if(Objects.isNull(entity.getCreatorId())){
            throw new ForbiddenException("You cannot update a Provider which derives from EOSC-Portal.");
        }

        ProviderMapper.INSTANCE.updateProviderFromDto(request, entity);

        return super.updateEntity(entity, id);
    }

    public ProjectionQuery<InstallationProjection> lookupInstallations(String from, String localField, String foreignField, String as, int page, int size, Class<InstallationProjection> projection) {

        throw new ForbiddenException("This operation has not been implemented yet.");
    }

    public ProjectionQuery<MetricProjection> fetchAllMetrics(String id, int page, int size){

        return providerAccessAlwaysRepository.fetchAllMetrics(id, page, size);


//        switch (getRequestInformation().getAccessType()){
//            case ALWAYS:
//                return providerAccessAlwaysRepository.fetchAllMetrics(id, page, size);
//            case ENTITY:
//                return providerAccessEntityRepository.fetchAllMetrics(id, page, size);
//            default:
//                throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
//        }
    }


    public boolean accessibility(String project, String provider, Collection collection, Operation operation){

                return providerAccessEntityRepository.accessibility(project, provider, collection, operation);
    }

    @Override
    public ProviderAccessAlwaysRepository always() {
        return providerAccessAlwaysRepository;
    }

    @Override
    public ProviderAccessEntityRepository entity() {
        return providerAccessEntityRepository;
    }
}
