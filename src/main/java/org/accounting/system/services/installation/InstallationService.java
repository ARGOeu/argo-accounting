package org.accounting.system.services.installation;

import io.quarkus.oidc.TokenIntrospection;
import org.accounting.system.dtos.acl.role.RoleAccessControlRequestDto;
import org.accounting.system.dtos.acl.role.RoleAccessControlResponseDto;
import org.accounting.system.dtos.acl.role.RoleAccessControlUpdateDto;
import org.accounting.system.dtos.installation.InstallationRequestDto;
import org.accounting.system.dtos.installation.InstallationResponseDto;
import org.accounting.system.dtos.installation.UpdateInstallationRequestDto;
import org.accounting.system.dtos.metric.MetricRequestDto;
import org.accounting.system.dtos.metric.MetricResponseDto;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.endpoints.InstallationEndpoint;
import org.accounting.system.entities.HierarchicalRelation;
import org.accounting.system.entities.authorization.Role;
import org.accounting.system.entities.installation.Installation;
import org.accounting.system.entities.projections.InstallationProjection;
import org.accounting.system.entities.projections.MetricProjection;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.mappers.AccessControlMapper;
import org.accounting.system.mappers.InstallationMapper;
import org.accounting.system.mappers.MetricMapper;
import org.accounting.system.repositories.HierarchicalRelationRepository;
import org.accounting.system.repositories.acl.AccessControlRepository;
import org.accounting.system.repositories.authorization.RoleRepository;
import org.accounting.system.repositories.installation.InstallationRepository;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.accounting.system.services.HierarchicalRelationService;
import org.accounting.system.services.acl.RoleAccessControlService;
import org.accounting.system.services.authorization.RoleService;
import org.accounting.system.util.QueryParser;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This service exposes business logic, which uses the {@link InstallationRepository}.
 * It is used to keep logic to a minimum in {@link InstallationEndpoint} and
 * {@link InstallationRepository}
 */
@ApplicationScoped
public class InstallationService implements RoleAccessControlService {

    @Inject
    InstallationRepository installationRepository;

    @Inject
    HierarchicalRelationService hierarchicalRelationService;

    @Inject
    RoleRepository roleRepository;

    @Inject
    AccessControlRepository accessControlRepository;

    @Inject
    TokenIntrospection tokenIntrospection;

    @Inject
    RoleService roleService;

    @Inject
    HierarchicalRelationRepository hierarchicalRelationRepository;

    @ConfigProperty(name = "key.to.retrieve.id.from.access.token")
    String id;

    @Inject
    QueryParser queryParser;

    /**
     * Maps the {@link InstallationRequestDto} to {@link Installation}.
     * Then the {@link Installation} is stored in the mongo database.
     *
     * @param request The POST request body.
     * @return The stored Installation has been turned into a response body.
     */
    public InstallationResponseDto save(InstallationRequestDto request) {

        installationRepository.exist(request.project, request.organisation, request.installation);

        var installation = installationRepository.save(request);

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

        var installation = fetchInstallationProjection(installationId);

        installationRepository.deleteInstallation(installation.getProject(), installation.getOrganisation(), installation.getId());

        hierarchicalRelationRepository.delete("externalId", installationId);
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

        var hierarchicalRelation = hierarchicalRelationRepository.find("externalId", id).firstResult();

        var ids = hierarchicalRelation.id.split(Pattern.quote(HierarchicalRelation.PATH_SEPARATOR));

        Installation installation = fetchInstallation(ids[0], ids[1], ids[2]);

        InstallationMapper.INSTANCE.updateInstallationFromDto(request, installation);

        if (!StringUtils.isAllBlank(installation.getProject(), installation.getOrganisation(), installation.getInstallation())) {
            installationRepository.exist(installation.getProject(), installation.getOrganisation(), installation.getInstallation());
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

    public PageResource<MetricProjection> fetchAllMetrics(String id, int page, int size, UriInfo uriInfo){

        var installation = fetchInstallation(id);

        var projection = hierarchicalRelationRepository.findByExternalId(installation.getProject() + HierarchicalRelation.PATH_SEPARATOR + installation.getOrganisation() + HierarchicalRelation.PATH_SEPARATOR + id, page, size);

        return new PageResource<>(projection, projection.list(), uriInfo);
    }

//    public PageResource< InstallationResponseDto> searchInstallation(String json, int page, int size, UriInfo uriInfo) throws NoSuchFieldException, org.json.simple.parser.ParseException {
//        ArrayList<String> installations = new ArrayList<>();
//
//        //find the installations of projects accessible
//        var projectIds = accessControlRepository.findByWhoAndCollection(tokenIntrospection.getJsonObject().getString(id), Collection.Project).stream().filter(projects ->
//                roleService.hasRoleAccess(projects.getRoles(), Collection.Project, Operation.READ)).map(projects -> projects.getEntity()).collect(Collectors.toList());
//
//        installations.addAll(hierarchicalRelationRepository.findInstallationsOfProjects(projectIds, "MetricDefinition", "unit_of_access", "_id", "unit_of_access").stream().map(InstallationProjection::convertIdToStr).collect(Collectors.toList()));
//        //find the installations of providers accessible
//        var providersIds = accessControlRepository.findByWhoAndCollection(tokenIntrospection.getJsonObject().getString(id), Collection.Provider).stream().collect(Collectors.toList());
//        providersIds.stream().map(RoleAccessControl::getEntity).map(providersId -> {
//            return providersId.trim().split("\\" + HierarchicalRelation.PATH_SEPARATOR);
//        }).forEach(inst -> {
//            installations.addAll(hierarchicalRelationRepository.findInstallationsByProvider(inst[0], inst[1], "MetricDefinition", "unit_of_access", "_id", "unit_of_access").stream().map(InstallationProjection::convertIdToStr).collect(Collectors.toList()));
//        });
//        //find the installations accessible
//        installations.addAll(accessControlRepository.findByWhoAndCollection(tokenIntrospection.getJsonObject().getString(id), Collection.Installation).stream().filter(inst -> roleService.hasRoleAccess(inst.getRoles(), Collection.Installation, Operation.READ)).map(inst -> inst.getEntity()).collect(Collectors.toList()));
//         //search on installations
//        Bson query = queryParser.parseFile(json, false, installations, Installation.class);
//
//        PanacheQuery<Installation> projectionQuery = installationRepository.search(query, page, size);
//        var installs = projectionQuery.list().stream().map(Installation::getId).map(instId -> {
//            return installationAccessAlwaysRepository.lookUpEntityById("MetricDefinition", "unit_of_access", "_id", "unit_of_access", InstallationProjection.class, instId);
//        }).collect(Collectors.toList());
//        return new PageResource<>(projectionQuery, InstallationMapper.INSTANCE.installationProjectionsToResponse(installs), uriInfo);
//    }


    @Override
    public void grantPermission(String who, RoleAccessControlRequestDto request, String... id) {

        var installationID = id[0];

        var hierarchicalRelation = hierarchicalRelationRepository.find("externalId", installationID).firstResult();

        var ids = hierarchicalRelation.id.split(Pattern.quote(HierarchicalRelation.PATH_SEPARATOR));

        var optional = installationRepository.fetchRoleAccessControl(ids[0], ids[1], ids[2], who);

        if(optional.isPresent()){

            throw new ConflictException("There is a Provider Access Control for the client : "+who);
        }

        var accessControl = AccessControlMapper.INSTANCE.requestToRoleAccessControl(request);

        accessControl.setWho(who);

        accessControl.setRoles(roleRepository.getRolesByName(request.roles));

        installationRepository.insertNewRoleAccessControl(ids[0], ids[1], ids[2], accessControl);
    }

    @Override
    public RoleAccessControlResponseDto modifyPermission(String who, RoleAccessControlUpdateDto updateDto, String... id) {

        var installationID = id[0];

        var hierarchicalRelation = hierarchicalRelationRepository.find("externalId", installationID).firstResult();

        var ids = hierarchicalRelation.id.split(Pattern.quote(HierarchicalRelation.PATH_SEPARATOR));

        var optional = installationRepository.fetchRoleAccessControl(ids[0], ids[1], ids[2], who);

        optional.orElseThrow(() -> new NotFoundException("There is no Access Control."));

        installationRepository.updateRoleAccessControl(ids[0], ids[1], ids[2], who, roleRepository.getRolesByName(updateDto.roles));

        var updated = installationRepository.fetchRoleAccessControl(ids[0], ids[1], ids[2], who);

        var response = AccessControlMapper.INSTANCE.roleAccessControlToResponse(updated.get());

        response.roles = updated.get().getRoles().stream().map(Role::getName).collect(Collectors.toSet());

        return response;
    }

    @Override
    public void deletePermission(String who, String... id) {

        var installationID = id[0];

        var hierarchicalRelation = hierarchicalRelationRepository.find("externalId", installationID).firstResult();

        var ids = hierarchicalRelation.id.split(Pattern.quote(HierarchicalRelation.PATH_SEPARATOR));

        var optional = installationRepository.fetchRoleAccessControl(ids[0], ids[1], ids[2], who);

        optional.orElseThrow(() -> new NotFoundException("There is no Access Control."));

        installationRepository.deleteRoleAccessControl(ids[0], ids[1], ids[2], who);
    }

    @Override
    public RoleAccessControlResponseDto fetchPermission(String who, String... id) {

        var installationID = id[0];

        var hierarchicalRelation = hierarchicalRelationRepository.find("externalId", installationID).firstResult();

        var ids = hierarchicalRelation.id.split(Pattern.quote(HierarchicalRelation.PATH_SEPARATOR));

        var optional = installationRepository.fetchRoleAccessControl(ids[0], ids[1], ids[2], who);

        optional.orElseThrow(() -> new NotFoundException("There is no Access Control."));

        var response = AccessControlMapper.INSTANCE.roleAccessControlToResponse(optional.get());

        response.roles = optional.get().getRoles().stream().map(Role::getName).collect(Collectors.toSet());

        return response;
    }

    @Override
    public PageResource<RoleAccessControlResponseDto> fetchAllPermissions(int page, int size, UriInfo uriInfo, String... id) {

        var installationID = id[0];

        var hierarchicalRelation = hierarchicalRelationRepository.find("externalId", installationID).firstResult();

        var ids = hierarchicalRelation.id.split(Pattern.quote(HierarchicalRelation.PATH_SEPARATOR));

        var panacheQuery = installationRepository.fetchAllRoleAccessControls(ids[0], ids[1], ids[2], page, size);

        var responses = panacheQuery
                .list()
                .stream()
                .map(acl->{

                    var response = AccessControlMapper.INSTANCE.roleAccessControlToResponse(acl);

                    response.roles = acl.getRoles().stream().map(Role::getName).collect(Collectors.toSet());

                    return response;

                })
                .collect(Collectors.toList());

        return new PageResource<>(panacheQuery, responses, uriInfo);
    }
}
