package org.accounting.system.services.installation;

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
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.mappers.AccessControlMapper;
import org.accounting.system.mappers.InstallationMapper;
import org.accounting.system.mappers.MetricMapper;
import org.accounting.system.repositories.HierarchicalRelationRepository;
import org.accounting.system.repositories.authorization.RoleRepository;
import org.accounting.system.repositories.installation.InstallationRepository;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.accounting.system.services.HierarchicalRelationService;
import org.accounting.system.services.acl.RoleAccessControlService;
import org.accounting.system.util.QueryParser;
import org.apache.commons.lang3.StringUtils;
import org.bson.conversions.Bson;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
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
    HierarchicalRelationRepository hierarchicalRelationRepository;

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
    public PageResource<InstallationResponseDto> getAllInstallations(int page, int size, UriInfo uriInfo){

        var projection = installationRepository.fetchAllInstallations( page, size);

        return new PageResource<>(projection, InstallationMapper.INSTANCE.installationProjectionsToResponse(projection.list()), uriInfo);

    }

    public PageResource<InstallationResponseDto>  searchInstallation(String json, int page, int size, UriInfo uriInfo) throws NoSuchFieldException, org.json.simple.parser.ParseException {
        Bson query = queryParser.parseFile(json, true, new ArrayList<>(), Installation.class);
        var projection = installationRepository.searchInstallations( query,page, size);

        return new PageResource<>(projection, InstallationMapper.INSTANCE.installationProjectionsToResponse(projection.list()), uriInfo);

    }

}

