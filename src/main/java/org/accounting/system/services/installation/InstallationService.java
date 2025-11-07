package org.accounting.system.services.installation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Payload;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.core.UriInfo;
import org.accounting.system.constraints.AccessInstallation;
import org.accounting.system.dtos.installation.InstallationRequestDto;
import org.accounting.system.dtos.installation.InstallationResponseDto;
import org.accounting.system.dtos.installation.UpdateInstallationRequestDto;
import org.accounting.system.dtos.metric.MetricRequestDto;
import org.accounting.system.dtos.metric.MetricResponseDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionResponseDto;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.endpoints.InstallationEndpoint;
import org.accounting.system.entities.HierarchicalRelation;
import org.accounting.system.entities.installation.Installation;
import org.accounting.system.entities.projections.InstallationProjection;
import org.accounting.system.entities.projections.InstallationReportNew;
import org.accounting.system.entities.projections.MetricProjection;
import org.accounting.system.enums.RelationType;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.mappers.InstallationMapper;
import org.accounting.system.mappers.MetricDefinitionMapper;
import org.accounting.system.mappers.MetricMapper;
import org.accounting.system.repositories.CapacityRepository;
import org.accounting.system.repositories.HierarchicalRelationRepository;
import org.accounting.system.repositories.installation.InstallationRepository;
import org.accounting.system.repositories.metric.MetricRepository;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.accounting.system.services.HierarchicalRelationService;
import org.accounting.system.services.ResourceService;
import org.accounting.system.services.groupmanagement.GroupManagementSelection;
import org.accounting.system.util.QueryParser;
import org.accounting.system.validators.AccessInstallationValidator;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * This service exposes business logic, which uses the {@link InstallationRepository}.
 * It is used to keep logic to a minimum in {@link InstallationEndpoint} and
 * {@link InstallationRepository}
 */
@ApplicationScoped
public class InstallationService {

    private static final Logger LOG = Logger.getLogger(InstallationService.class);


    @Inject
    InstallationRepository installationRepository;

    @Inject
    HierarchicalRelationService hierarchicalRelationService;

    @Inject
    HierarchicalRelationRepository hierarchicalRelationRepository;
    @Inject
    QueryParser queryParser;

    @Inject
    ResourceService resourceService;

    @Inject
    GroupManagementSelection groupManagementSelection;

    @Inject
    MetricRepository metricRepository;

    @Inject
    CapacityRepository capacityRepository;

    /**
     * Maps the {@link InstallationRequestDto} to {@link Installation}.
     * Then the {@link Installation} is stored in the mongo database.
     *
     * @param request The POST request body.
     * @return The stored Installation has been turned into a response body.
     */
    public InstallationResponseDto save(InstallationRequestDto request, String creatorId) {

        var optional = installationRepository.exist(request.project, request.organisation, request.installation);

        optional.ifPresent(storedInstallation -> {throw new ConflictException("There is an Installation with the following combination : {"+request.project+", "+request.organisation+", "+request.installation+"}. Its id is "+storedInstallation.getId().toString());});

        if(StringUtils.isNotEmpty(request.resource) && ! resourceService.exist(request.resource)){
            throw new NotFoundException("There is no Resource with the following id: "+request.resource);
        }

        if (StringUtils.isEmpty(request.externalId)) {
            request.externalId = UUID.randomUUID().toString();
        }

        if (installationRepository.fetchInstallationByExternalId(request.project, request.organisation, request.externalId).isPresent()) {
            throw new ConflictException("external_id already exists");
        }

        var installation = installationRepository.save(request, creatorId);

        try{

            groupManagementSelection.from().choose().createInstallationGroup(request.project, request.organisation, installation.getId());
        } catch (Exception e){

            LOG.error("Group creation failed with error : " + e.getMessage());
            delete(installation.getId());
            throw new ServerErrorException("Group creation failed with error : " + e.getMessage(), 500);
        }

        var installationProjection = fetchInstallationProjection(installation.getProject(), installation.getOrganisation(), installation.getId());

        return InstallationMapper.INSTANCE.installationProjectionToResponse(installationProjection);
    }

    /**
     * Delete an Installation by given id.
     *
     * @param installationId The Metric Definition to be deleted.
     * @return If the operation is successful or not.
     */
    public void delete(String installationId) {

        if(hierarchicalRelationRepository.hasRelationMetrics(installationId)){

            throw new ForbiddenException("Deleting an Installation is not allowed if there are Metrics assigned to it.");
        }

        var installation = fetchInstallationProjection(installationId);

        installationRepository.deleteInstallation(installation.getProject(), installation.getOrganisation(), installation.getId());

        hierarchicalRelationRepository.delete("externalId", installationId);

        groupManagementSelection.from().choose().deleteInstallationGroup(installation.getProject(), installation.getOrganisation(), installation.getId());

        capacityRepository.deleteByInstallationId(installationId);
    }

    /**
     * Turns an InstallationProjection into an InstallationResponseDto.
     *
     * @param id The Installation id.
     * @return The corresponding Installation transformed to response dto.
     */
    public InstallationResponseDto installationToResponse(String id) {

        var installation = fetchInstallationProjection(id);

        return InstallationMapper.INSTANCE.installationProjectionToResponse(installation);
    }

    /**
     * Fetches an InstallationProjection belonging to specific Project-Provider from Project Collection.
     * It gets the Project-Provider-Installation relationship from HierarchicalRelation Collection.
     *
     * @param id The Installation id.
     * @return The corresponding InstallationProjection.
     */
    public InstallationProjection fetchInstallationProjection(String id) {

        var hierarchicalRelation = hierarchicalRelationRepository.find("externalId", id).firstResult();

        var ids = hierarchicalRelation.id.split(Pattern.quote(HierarchicalRelation.PATH_SEPARATOR));

        return fetchInstallationProjection(ids[0], ids[1], ids[2]);
    }

    /**
     * Fetches an Installation belonging to specific Project-Provider from Project Collection.
     * It gets the Project-Provider-Installation relationship from HierarchicalRelation Collection.
     *
     * @param id The Installation id.
     * @return The corresponding Installation.
     */
    public Installation fetchInstallation(String id) {

        var hierarchicalRelation = hierarchicalRelationRepository.find("externalId", id).firstResult();

        var ids = hierarchicalRelation.id.split(Pattern.quote(HierarchicalRelation.PATH_SEPARATOR));

        return installationRepository.fetchInstallation(ids[0], ids[1], ids[2]);
    }

    public Optional<HierarchicalRelation> fetchExternalHierarchicalRelation(String externalId){

        return hierarchicalRelationRepository.find("externalUniqueIdentifier = ?1 and relationType = ?2", externalId, RelationType.INSTALLATION).firstResultOptional();

    }

    public InstallationResponseDto fetchExternalInstallation(String externalId) {

        var optional = fetchExternalHierarchicalRelation(externalId);

        if(optional.isEmpty()){
            throw new NotFoundException("There is no Installation with external id: " +externalId);
        }

        var ids = optional.get().id.split(Pattern.quote(HierarchicalRelation.PATH_SEPARATOR));

        var validator = getAccessInstallationValidator(new String[] {"admin", "viewer"});

        validator.isValid(ids[2], null);

        var installation = fetchInstallationProjection(ids[0], ids[1], ids[2]);

        return InstallationMapper.INSTANCE.installationProjectionToResponse(installation);
    }

    public void deleteExternalInstallation(String externalId) {

        var optional = fetchExternalHierarchicalRelation(externalId);

        if(optional.isEmpty()){
            throw new NotFoundException("There is no Installation with external id: " +externalId);
        }

        var ids = optional.get().id.split(Pattern.quote(HierarchicalRelation.PATH_SEPARATOR));

        if(hierarchicalRelationRepository.hasRelationMetrics(ids[2])){

            throw new ForbiddenException("Deleting an Installation is not allowed if there are Metrics assigned to it.");
        }

        var validator = getAccessInstallationValidator(new String[] {"admin"});

        validator.isValid(ids[2], null);


        var installation = fetchInstallationProjection(ids[0], ids[1], ids[2]);

        installationRepository.deleteInstallation(installation.getProject(), installation.getOrganisation(), installation.getId());

        hierarchicalRelationRepository.delete("externalId", ids[2]);
    }

    public InstallationResponseDto updateExternalInstallation(String externalId, UpdateInstallationRequestDto request) {

        var optional = fetchExternalHierarchicalRelation(externalId);

        if(optional.isEmpty()){
            throw new NotFoundException("There is no Installation with external id: " +externalId);
        }

        var ids = optional.get().id.split(Pattern.quote(HierarchicalRelation.PATH_SEPARATOR));

        var validator = getAccessInstallationValidator(new String[] {"admin"});

        validator.isValid(ids[2], null);

        if(hierarchicalRelationRepository.hasRelationMetrics(ids[2])){

            throw new ForbiddenException("Updating an Installation is not allowed if there are Metrics assigned to it.");
        }

        if(StringUtils.isNotEmpty(request.resource) && ! resourceService.exist(request.resource)){

            throw new NotFoundException("There is no Resource with the following id: "+request.resource);
        }

        Installation installation = fetchInstallation(ids[0], ids[1], ids[2]);

        InstallationMapper.INSTANCE.updateInstallationFromDto(request, installation);

        if (StringUtils.isNoneEmpty( request.installation)) {

            var temp = installationRepository.exist(installation.getProject(), installation.getOrganisation(), request.installation);

            if(temp.isPresent() && !temp.get().getId().equals(ids[2])){

                throw new ConflictException("There is an Installation with the following combination : {"+installation.getProject()+", "+installation.getOrganisation()+", "+request.installation+"}. Its id is "+ temp.get().getId());}
        }

        installationRepository.updateInstallation(ids[0], ids[1], ids[2], installation);

        var installationProjection = fetchInstallationProjection(ids[2]);

        return InstallationMapper.INSTANCE.installationProjectionToResponse(installationProjection);
    }

    public InstallationReportNew externalInstallationReport(String externalId, String start, String end){

        var optional = fetchExternalHierarchicalRelation(externalId);

        if(optional.isEmpty()){
            throw new NotFoundException("There is no Installation with external id: " +externalId);
        }

        var ids = optional.get().id.split(Pattern.quote(HierarchicalRelation.PATH_SEPARATOR));

        var validator = getAccessInstallationValidator(new String[] {"admin", "viewer"});

        validator.isValid(ids[2], null);

        var installation = fetchInstallation(ids[2]);

        return installationRepository.aggregateMetricsByDefinition(installation, start, end);
    }

    public MetricResponseDto assignMetricToExternalInstallation(String externalId, MetricRequestDto request) {

        var optional = fetchExternalHierarchicalRelation(externalId);

        if(optional.isEmpty()){
            throw new NotFoundException("There is no Installation with external id: " +externalId);
        }

        var ids = optional.get().id.split(Pattern.quote(HierarchicalRelation.PATH_SEPARATOR));

        var validator = getAccessInstallationValidator(new String[] {"admin"});

        validator.isValid(ids[2], null);

        var metric = hierarchicalRelationService.assignMetric(ids[2], request);

        return MetricMapper.INSTANCE.metricToResponse(metric);
    }

    public PageResource<MetricProjection> fetchAllMetricsByExternalId(String externalId, int page, int size, UriInfo uriInfo, String start, String end, String metricDefinitionId){

        var optional = fetchExternalHierarchicalRelation(externalId);

        if(optional.isEmpty()){
            throw new NotFoundException("There is no Installation with external id: " +externalId);
        }

        var ids = optional.get().id.split(Pattern.quote(HierarchicalRelation.PATH_SEPARATOR));

        var validator = getAccessInstallationValidator(new String[] {"admin", "viewer"});

        validator.isValid(ids[2], null);

        var projection = metricRepository.findByExternalId(ids[0] + HierarchicalRelation.PATH_SEPARATOR + ids[1] + HierarchicalRelation.PATH_SEPARATOR + ids[2], page, size, start, end, metricDefinitionId);

        return new PageResource<>(projection, projection.list(), uriInfo);
    }

    private static AccessInstallationValidator getAccessInstallationValidator(String[] roles) {
        var accessInstallation = new AccessInstallation(){

            @Override
            public Class<? extends Annotation> annotationType() {
                return AccessInstallation.class;
            }

            @Override
            public String message() {
                return "The authenticated client is not permitted to perform the requested operation.";
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return new Class[0];
            }

            @Override
            public String[] roles() {
                return roles;
            }
        };

        var validator = new AccessInstallationValidator();
        validator.initialize(accessInstallation);
        return validator;
    }

    /**
     * Fetches an Installation belonging to specific Project-Provider from Project Collection.
     *
     * @param projectID The Project id.
     * @param providerID The Provider id.
     * @param installationID The Installation id.
     * @return The corresponding Installation.
     */
    public Installation fetchInstallation(String projectID, String providerID, String installationID) {

        return installationRepository.fetchInstallation(projectID, providerID, installationID);
    }


    /**
     * Fetches an InstallationProjection belonging to specific Project-Provider from Project Collection.
     *
     * @param projectID The Project id.
     * @param providerID The Provider id.
     * @param installationID The Installation id.
     * @return The corresponding InstallationProjection.
     */
    public InstallationProjection fetchInstallationProjection(String projectID, String providerID, String installationID) {

        return installationRepository.fetchInstallationProjection(projectID, providerID, installationID);
    }

    /**
     * This method is responsible for checking if there is a Provider with given name or id.
     * Then, it calls the {@link ProviderRepository providerRepository} to update a Provider.
     *
     * @param id      The Installation to be updated.
     * @param request The Installation attributes to be updated.
     * @return The updated Installation.
     */
    public InstallationResponseDto update(String id, UpdateInstallationRequestDto request) {

        if(hierarchicalRelationRepository.hasRelationMetrics(id)){

            throw new ForbiddenException("Updating an Installation is not allowed if there are Metrics assigned to it.");
        }

        if(StringUtils.isNotEmpty(request.resource) && ! resourceService.exist(request.resource)){

            throw new NotFoundException("There is no Resource with the following id: "+request.resource);
        }

        var hierarchicalRelation = hierarchicalRelationRepository.find("externalId", id).firstResult();

        var ids = hierarchicalRelation.id.split(Pattern.quote(HierarchicalRelation.PATH_SEPARATOR));

        Installation installation = fetchInstallation(ids[0], ids[1], ids[2]);

        InstallationMapper.INSTANCE.updateInstallationFromDto(request, installation);

        if (StringUtils.isNoneEmpty(request.installation)) {

            var temp = installationRepository.exist(installation.getProject(), installation.getOrganisation(), request.installation);

            if(temp.isPresent() && !temp.get().getId().equals(ids[2])){

                throw new ConflictException("There is an Installation with the following combination : {"+installation.getProject()+", "+installation.getOrganisation()+", "+request.installation+"}. Its id is "+ temp.get().getId());}
        }

        installationRepository.updateInstallation(ids[0], ids[1], ids[2], installation);

        var installationProjection = fetchInstallationProjection(id);

        return InstallationMapper.INSTANCE.installationProjectionToResponse(installationProjection);
    }

    /**
     * This method is responsible for creating a hierarchical structure which consists of :
     * - Root -> Project
     * - Intermediate Level -> Provider
     * - Leaf -> Installation
     * The Metric is assigned to the given installation (leaf)
     *
     * @param installationId Leaf of structure
     * @param request        Metric to be created and assigned to the given installation
     * @return The assigned Metric
     */
    public MetricResponseDto assignMetric(String installationId, MetricRequestDto request) {

        var metric = hierarchicalRelationService.assignMetric(installationId, request);

        return MetricMapper.INSTANCE.metricToResponse(metric);
    }

    public PageResource<InstallationResponseDto> getAllInstallations(int page, int size, UriInfo uriInfo){

        var projection = installationRepository.fetchAllInstallations( page, size);

        return new PageResource<>(projection, InstallationMapper.INSTANCE.installationProjectionsToResponse(projection.list()), uriInfo);

    }

    public PageResource<InstallationResponseDto>  searchInstallation(String json, int page, int size, UriInfo uriInfo) throws org.json.simple.parser.ParseException {

        var query = queryParser.parseFile(json);
        var projection = installationRepository.searchInstallations( query,page, size);
        return new PageResource<>(projection, InstallationMapper.INSTANCE.installationProjectionsToResponse(projection.list()), uriInfo);
    }

    public PageResource<MetricDefinitionResponseDto> fetchAllMetricDefinitions(String id, int page, int size, UriInfo uriInfo){

        var projection = installationRepository.fetchAllMetricDefinitions(id, page, size);

        return new PageResource<>(projection, MetricDefinitionMapper.INSTANCE.metricDefinitionsToResponse(projection.list()), uriInfo);
    }

    public InstallationReportNew installationReport(String installationId, String start, String end){

        var installation = fetchInstallation(installationId);

        return installationRepository.aggregateMetricsByDefinition(installation, start, end);
    }
}

