package org.accounting.system.services;

import io.quarkus.mongodb.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.core.UriInfo;
import org.accounting.system.dtos.installation.InstallationResponseDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionResponseDto;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.dtos.provider.ProviderRequestDto;
import org.accounting.system.dtos.provider.ProviderResponseDto;
import org.accounting.system.dtos.provider.UpdateProviderRequestDto;
import org.accounting.system.entities.projections.InstallationProjection;
import org.accounting.system.entities.projections.ProviderProjectionWithProjectInfo;
import org.accounting.system.entities.projections.ProviderReport;
import org.accounting.system.entities.provider.Provider;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.mappers.InstallationMapper;
import org.accounting.system.mappers.MetricDefinitionMapper;
import org.accounting.system.mappers.ProviderMapper;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.accounting.system.services.clients.GroupRequest;
import org.accounting.system.util.QueryParser;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProviderService {

    private static final Logger LOG = Logger.getLogger(ProviderService.class);

    @Inject
    ProviderRepository providerRepository;

    @Inject
    HierarchicalRelationService hierarchicalRelationService;
    @Inject
    QueryParser queryParser;

    @Inject
    KeycloakService keycloakService;


    /**
     * Returns the N Providers from the given page.
     *
     * @param page Indicates the page number.
     * @param size The number of Providers to be retrieved.
     * @param uriInfo The current uri.
     * @return An object represents the paginated results.
     */
    public PageResource<ProviderResponseDto> findAllProvidersPageable(int page, int size, UriInfo uriInfo){

        PanacheQuery<Provider> panacheQuery = providerRepository.findAllPageable(page, size);

        return new PageResource<>(panacheQuery, ProviderMapper.INSTANCE.providersToResponse(panacheQuery.list()), uriInfo);
    }

    /**
     * Maps the {@link ProviderRequestDto} to {@link Provider}.
     * Then the {@link Provider} is stored in the mongo database.
     *
     * @param request The POST request body.
     * @return The stored Provider has been turned into a response body.
     */
    public ProviderResponseDto save(ProviderRequestDto request) {

        var group = keycloakService.getValueByKey("/accounting/roles/provider");

        if(Objects.isNull(group)){

            throw new ServerErrorException("The subgroup /accounting/roles/provider does not exist, so the provider cannot be saved. Please, create the subgroup /accounting/roles/provider!", 500);
        }

        var provider = ProviderMapper.INSTANCE.requestToProvider(request);

        provider.setRegisteredOn(LocalDateTime.now());

        providerRepository.save(provider);

        try{
            var groupRequest = new GroupRequest();
            groupRequest.name = provider.getId();

            GroupRequest.Attributes attrs = new GroupRequest.Attributes();
            attrs.description = List.of(provider.getId());

            groupRequest.attributes = attrs;

            keycloakService.createSubGroup(group, groupRequest);
            var id = keycloakService.getValueByKey("/accounting/roles/provider/"+provider.getId());

            keycloakService.addRole(id, "admin");
            keycloakService.addRole(id, "viewer");

            var defaultConfigurationId = keycloakService.getValueByKey(id);
            var defaultConfiguration = keycloakService.getConfiguration(id, defaultConfigurationId);
            var groupRoles = List.of("admin", "viewer");
            defaultConfiguration.setGroupRoles(groupRoles);
            keycloakService.updateConfiguration(id, defaultConfiguration);
        } catch (Exception e){

            LOG.error("Group creation failed with error : " + e.getMessage());
            providerRepository.deleteById(provider.getId());
            throw new ServerErrorException("Group creation failed with error : " + e.getMessage(), 500);
        }

        return ProviderMapper.INSTANCE.providerToResponse(provider);
    }

    /**
     * Checks if a Provider with given id exists.
     *
     * @param id The Provider id.
     * @throws ConflictException If Provider already exists.
     */
    public void existById(String id){

        providerRepository.findByIdOptional(id)
                .ifPresent(provider -> {throw new ConflictException("There is a Provider with id: "+id);});
    }

    /**
     * Checks if a Provider with given name exists.
     *
     * @param name The Provider name.
     * @throws ConflictException If Provider already exists.
     */
    public void existByName(String name){

        providerRepository.findByName(name)
                .ifPresent(provider -> {throw new ConflictException("There is a Provider with name: "+name);});
    }

    /**
     * Delete a Provider by given id.
     * @param providerId The Provider to be deleted.
     * @return If the operation is successful or not.
     * @throws ForbiddenException If the Providers derive from EOSC-Portal.
     */
    public boolean delete(String providerId){

        var provider = providerRepository.findById(providerId);

        // if Provider's creator id is null then it derives from EOSC-Portal
        if(StringUtils.isEmpty(provider.getCreatorId())){
            throw new ForbiddenException("You cannot delete a Provider which derives from EOSC-Portal.");
        }

        if(hierarchicalRelationService.providerBelongsToAnyProject(providerId)){
            throw new ForbiddenException("You cannot delete a Provider which belongs to a Project.");
        }

        try{

            keycloakService.deleteGroup(keycloakService.getValueByKey("/accounting/roles/provider/"+providerId));
        } catch (Exception e){

            LOG.error("Group deletion failed with error : " + e.getMessage());
        }

        return providerRepository.deleteEntityById(providerId);
    }

    /**
     * This method is responsible for checking if there is a Provider with given name or id.
     * Then, it calls the {@link ProviderRepository providerRepository} to update a Provider.
     *
     * @param id The Provider to be updated.
     * @param request The Provider attributes to be updated.
     * @return The updated Provider.
     * @throws ConflictException If Provider with the given name or id already exists.
     */
    public ProviderResponseDto update(String id, UpdateProviderRequestDto request){

        if(StringUtils.isNotEmpty(request.id)){
            existById(request.id);
        }

        if(StringUtils.isNotEmpty(request.name)){
            existByName(request.name);
        }

        var provider = providerRepository.updateEntity(id, request);

        return ProviderMapper.INSTANCE.providerToResponse(provider);
    }

    /**
     * Fetches a Provider by given id.
     *
     * @param id The Provider id.
     * @return The corresponding Provider.
     */
    public ProviderResponseDto fetchProvider(String id){

        var provider = providerRepository.fetchEntityById(id);

        return ProviderMapper.INSTANCE.providerToResponse(provider);
    }

    /**
     * This method check whether the given Provider derived from EOSC-Portal or not.
     *
     * @param id The Provider ID.
     * @throws ForbiddenException If provider derives from EOSC-Portal
     */
    public void derivesFromEoscPortal(String id){

        Provider entity = providerRepository.findById(id);

        // if Provider's creator id is null then it derives from EOSC-Portal
        if(Objects.isNull(entity.getCreatorId())){
            throw new ForbiddenException("You cannot access a Provider which derives from EOSC-Portal.");
        }
    }

    public PageResource<InstallationResponseDto> findInstallationsByProvider(String projectId, String providerId, int page, int size, UriInfo uriInfo){

        PanacheQuery<InstallationProjection> projectionQuery = providerRepository.fetchProviderInstallations(projectId, providerId, page, size);

        return new PageResource<>(projectionQuery, InstallationMapper.INSTANCE.installationProjectionsToResponse(projectionQuery.list()), uriInfo);
    }

    public  PageResource< ProviderResponseDto> searchProviders(String json, int page, int size, UriInfo uriInfo) throws org.json.simple.parser.ParseException {

        var query = queryParser.parseFile(json);
        var projectionQuery = providerRepository.searchProviders(query,page,size);
        return new PageResource<>(projectionQuery, ProviderMapper.INSTANCE.providersToResponse(projectionQuery.list()), uriInfo);
    }

    public PageResource<ProviderProjectionWithProjectInfo> getSystemProviders(int page, int size, UriInfo uriInfo)  {

        var projectionQuery = providerRepository.fetchSystemProviders(page, size);

        return new PageResource<>(projectionQuery, projectionQuery.list(), uriInfo);
    }

    public PageResource<MetricDefinitionResponseDto> fetchAllMetricDefinitions(String projectId, String providerId, int page, int size, UriInfo uriInfo){

        var projection = providerRepository.fetchAllMetricDefinitions(projectId, providerId, page, size);

        return new PageResource<>(projection, MetricDefinitionMapper.INSTANCE.metricDefinitionsToResponse(projection.list()), uriInfo);
    }

    public ProviderReport providerReport(String projectId, String providerId, String start, String end){

        return providerRepository.providerReport(projectId, providerId, start, end);
    }

    public List<ProviderReport> providerReport(String providerId, String start, String end){

        return access(providerId, start, end);
    }

    private List<ProviderReport> access(String providerId, String start, String end){

        var projects = hierarchicalRelationService.getProjectsByProvider(providerId);

        return  projects.stream().map(project->providerRepository.providerReport(project, providerId, start, end)).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
