package org.accounting.system.services;

import com.mongodb.client.model.Filters;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.vavr.collection.Array;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Payload;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.core.UriInfo;
import org.accounting.system.constraints.AccessProvider;
import org.accounting.system.dtos.installation.InstallationResponseDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionResponseDto;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.dtos.provider.ProviderRequestDto;
import org.accounting.system.dtos.provider.ProviderResponseDto;
import org.accounting.system.dtos.provider.UpdateProviderRequestDto;
import org.accounting.system.entities.HierarchicalRelation;
import org.accounting.system.entities.projections.CapacityPeriod;
import org.accounting.system.entities.projections.GenericProviderReport;
import org.accounting.system.entities.projections.MetricGroupResults;
import org.accounting.system.entities.projections.MetricProjection;
import org.accounting.system.entities.projections.MetricReportProjection;
import org.accounting.system.entities.projections.ProviderProjectionWithProjectInfo;
import org.accounting.system.entities.projections.ProviderReport;
import org.accounting.system.entities.provider.Provider;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.mappers.InstallationMapper;
import org.accounting.system.mappers.MetricDefinitionMapper;
import org.accounting.system.mappers.ProviderMapper;
import org.accounting.system.repositories.installation.InstallationRepository;
import org.accounting.system.repositories.metric.MetricRepository;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.accounting.system.services.groupmanagement.GroupManagementSelection;
import org.accounting.system.util.QueryParser;
import org.accounting.system.validators.AccessProviderValidator;
import org.apache.commons.lang3.StringUtils;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

import java.lang.annotation.Annotation;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
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
    GroupManagementSelection groupManagementSelection;

    @Inject
    MetricRepository metricRepository;

    @Inject
    InstallationRepository installationRepository;

    /**
     * Returns the N Providers from the given page.
     *
     * @param page    Indicates the page number.
     * @param size    The number of Providers to be retrieved.
     * @param uriInfo The current uri.
     * @return An object represents the paginated results.
     */
    public PageResource<ProviderResponseDto> findAllProvidersPageable(int page, int size, UriInfo uriInfo) {

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
    public ProviderResponseDto save(ProviderRequestDto request, String creatorId) {

        if (StringUtils.isEmpty(request.externalId)) {
            request.externalId = UUID.randomUUID().toString();
        }

        if (providerRepository.findByExternalId(request.externalId).isPresent()) {
            throw new ConflictException("external_id already exists");
        }

        var provider = new Provider();

        provider.setRegisteredOn(LocalDateTime.now());

        provider.setId(new ObjectId().toString());

        provider.setCreatorId(creatorId);

        provider.setWebsite(request.website);

        provider.setLogo(request.logo);

        provider.setName(request.name);

        provider.setExternalId(request.externalId);

        provider.setAbbreviation(request.abbreviation);

        providerRepository.save(provider);

        try {

            groupManagementSelection.from().choose().createProviderGroup(provider.getId());

        } catch (Exception e) {

            LOG.error("Group creation failed with error : " + e.getMessage());
            providerRepository.deleteById(provider.getId());
            throw new ServerErrorException("Group creation failed with error : " + e.getMessage(), 500);
        }

        return ProviderMapper.INSTANCE.providerToResponse(provider);
    }

    /**
     * Checks if a Provider with given name exists.
     *
     * @param name The Provider name.
     * @throws ConflictException If Provider already exists.
     */
    public void existByName(String name) {

        providerRepository.findByName(name)
                .ifPresent(provider -> {
                    throw new ConflictException("There is a Provider with name: " + name);
                });
    }

    /**
     * Delete a Provider by given id.
     *
     * @param providerId The Provider to be deleted.
     * @return If the operation is successful or not.
     * @throws ForbiddenException If the Providers derive from EOSC-Portal.
     */
    public boolean delete(String providerId) {

        var provider = providerRepository.findById(providerId);

        if (StringUtils.isEmpty(provider.getCreatorId())) {
            throw new ForbiddenException("You cannot delete a Provider which derives from EOSC-Portal.");
        }

        if (hierarchicalRelationService.providerBelongsToAnyProject(providerId)) {
            throw new ForbiddenException("You cannot delete a Provider which belongs to a Project.");
        }

        groupManagementSelection.from().choose().deleteProviderGroup(provider.getId());

        return providerRepository.deleteEntityById(providerId);
    }

    /**
     * This method is responsible for checking if there is a Provider with given name or id.
     * Then, it calls the {@link ProviderRepository providerRepository} to update a Provider.
     *
     * @param id      The Provider to be updated.
     * @param request The Provider attributes to be updated.
     * @return The updated Provider.
     * @throws ConflictException If Provider with the given name or id already exists.
     */
    public ProviderResponseDto update(String id, UpdateProviderRequestDto request) {

        if (StringUtils.isNotEmpty(request.name)) {

            var optional = providerRepository.findByName(request.name);

            if (optional.isPresent() && !optional.get().getId().equals(id)) {

                throw new ConflictException("There is a Provider with name: " + request.name);
            }
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
    public ProviderResponseDto fetchProvider(String id) {

        var provider = providerRepository.fetchEntityById(id);

        return ProviderMapper.INSTANCE.providerToResponse(provider);
    }

    public ProviderResponseDto fetchProviderByExternal(String externalProviderId) {

        var optional = providerRepository.findByExternalId(externalProviderId);

        if (optional.isEmpty()) {
            throw new NotFoundException("There is no Provider with external id: " + externalProviderId);
        }

        return ProviderMapper.INSTANCE.providerToResponse(optional.get());
    }

    public PageResource<InstallationResponseDto> findInstallationsByProvider(String projectId, String providerId, int page, int size, UriInfo uriInfo) {

        var projectionQuery = providerRepository.fetchProviderInstallations(projectId, providerId, page, size);

        return new PageResource<>(projectionQuery, InstallationMapper.INSTANCE.installationProjectionsToResponse(projectionQuery.list()), uriInfo);
    }

    public PageResource<ProviderResponseDto> searchProviders(String json, int page, int size, UriInfo uriInfo) throws org.json.simple.parser.ParseException {

        var query = queryParser.parseFile(json);
        var projectionQuery = providerRepository.searchProviders(query, page, size);
        return new PageResource<>(projectionQuery, ProviderMapper.INSTANCE.providersToResponse(projectionQuery.list()), uriInfo);
    }

    public PageResource<ProviderProjectionWithProjectInfo> getSystemProviders(int page, int size, UriInfo uriInfo) {

        var projectionQuery = providerRepository.fetchSystemProviders(page, size);

        return new PageResource<>(projectionQuery, projectionQuery.list(), uriInfo);
    }

    public PageResource<MetricDefinitionResponseDto> fetchAllMetricDefinitions(String projectId, String providerId, int page, int size, UriInfo uriInfo) {

        var projection = providerRepository.fetchAllMetricDefinitions(projectId, providerId, page, size);

        return new PageResource<>(projection, MetricDefinitionMapper.INSTANCE.metricDefinitionsToResponse(projection.list()), uriInfo);
    }

    public ProviderReport providerReport(String projectId, String providerId, String start, String end){

        return providerReport(projectId, providerId, start, end, Array.of());
    }

    public ProviderReport providerReport(String projectId, String providerId, String start, String end, Array<Bson> filters) {

        var provider = providerRepository.findById(providerId);

        var installations = providerRepository.fetchAllProviderInstallations(projectId, providerId);

        var installationReports = installations
                .stream()
                .map(installation -> installationRepository.installationReport(installation, start, end, filters.append(Filters.regex("resource_id","^"+ installation.getProject() + HierarchicalRelation.PATH_SEPARATOR + installation.getOrganisation() + HierarchicalRelation.PATH_SEPARATOR + installation.getId() + "(?:\\.[^\\r\\n.]+)*$")))).collect(Collectors.toList());

        var aggregatedMetrics = installationReports
                .stream()
                .flatMap(rpt -> rpt.data.stream())
                .collect(Collectors.toMap(MetricGroupResults::getMetricDefinitionId,
                        m -> {
                            var copy = new MetricReportProjection();
                            copy.metricDefinitionId = m.getMetricDefinitionId();
                            copy.metricName = m.getMetricName();
                            copy.metricDescription = m.getMetricDescription();
                            copy.unitType = m.getUnitType();
                            copy.metricType = m.getMetricType();
                            copy.totalValue = m.getPeriods().stream().map(CapacityPeriod::getTotalValue).mapToDouble(Double::doubleValue).sum();
                            return copy;
                        },
                        (m1, m2) -> {
                            m1.totalValue = m1.totalValue + m2.totalValue;
                            return m1;
                        }
                ));


        var report = new ProviderReport();
        report.providerId = providerId;
        report.name = provider.getName();
        report.logo = provider.getLogo();
        report.abbreviation = provider.getAbbreviation();
        report.externalId = provider.getExternalId();
        report.website = provider.getWebsite();
        report.data = installationReports;
        report.aggregatedMetrics = new ArrayList<>(aggregatedMetrics.values());

        return report;
    }

    public ProviderReport providerReportByExternalId(String projectId, String externalProviderId, String start, String end) {

        var optional = providerRepository.findByExternalId(externalProviderId);

        if (optional.isEmpty()) {
            throw new NotFoundException("There is no Provider with external id: " + externalProviderId);
        }

        var provider = optional.get();

        var validator = getAccessProviderValidator(new String[]{"admin", "viewer"});

        validator.isValid(new String[]{projectId, provider.getId()}, null);

        return providerReport(projectId, provider.getId(), start, end);
    }

    public PageResource<MetricProjection> fetchAllMetricsByExternalProviderId(String projectId, String externalProviderId, int page, int size, UriInfo uriInfo, String start, String end, String metricDefinitionId) {

        var optional = providerRepository.findByExternalId(externalProviderId);

        if (optional.isEmpty()) {
            throw new NotFoundException("There is no Provider with external id: " + externalProviderId);
        }

        var provider = optional.get();

        var validator = getAccessProviderValidator(new String[]{"admin", "viewer"});

        validator.isValid(new String[]{projectId, provider.getId()}, null);

        var projection = metricRepository.findByExternalId(projectId + HierarchicalRelation.PATH_SEPARATOR + provider.getId(), page, size, start, end, metricDefinitionId);

        return new PageResource<>(projection, projection.list(), uriInfo);
    }

    private static AccessProviderValidator getAccessProviderValidator(String[] roles) {
        var accessProvider = new AccessProvider() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return AccessProvider.class;
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

        var validator = new AccessProviderValidator();
        validator.initialize(accessProvider);
        return validator;
    }

    public Optional<Provider> fetchByExternalId(String externalId) {

        return providerRepository.findByExternalId(externalId);
    }

    public GenericProviderReport providerReport(String providerId, String start, String end) {

        var reports = access(providerId, start, end);

        var grouped = new ArrayList<>(
                reports.stream().flatMap(r -> r.aggregatedMetrics.stream())
                        .collect(Collectors.toMap(
                                m -> m.metricDefinitionId,
                                m -> m,
                                (m1, m2) -> {
                                    var combined = new MetricReportProjection();
                                    combined.metricDefinitionId = m1.metricDefinitionId;
                                    combined.metricName = m1.metricName;
                                    combined.metricDescription = m1.metricDescription;
                                    combined.unitType = m1.unitType;
                                    combined.metricType = m1.metricType;
                                    combined.totalValue = m1.totalValue + m2.totalValue;
                                    return combined;
                                }
                        ))
                        .values()
        );

        var genericReport = new GenericProviderReport();

        genericReport.reports = reports;
        genericReport.aggregatedMetrics = grouped;

        return genericReport;
    }

    private List<ProviderReport> access(String providerId, String start, String end) {

        var projects = hierarchicalRelationService.getProjectsByProvider(providerId);

        return projects.stream().map(project -> providerReport(project, providerId, start, end)).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
