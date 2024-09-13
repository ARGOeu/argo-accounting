package org.accounting.system.services.client;

import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.UriInfo;
import org.accounting.system.beans.RequestUserContext;
import org.accounting.system.dtos.authorization.CollectionAccessPermissionDto;
import org.accounting.system.dtos.client.ClientResponseDto;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.entities.client.Client;
import org.accounting.system.mappers.ClientMapper;
import org.accounting.system.repositories.authorization.RoleRepository;
import org.accounting.system.repositories.client.ClientRepository;
import org.accounting.system.services.authorization.RoleService;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class ClientService {

    @Inject
    ClientRepository clientRepository;

    @Inject
    RoleRepository roleRepository;

    @Inject
    RoleService roleService;

    @Inject
    RequestUserContext requestUserContext;

    /**
     * This method extracts specific information regarding the client from access token and stores it into the
     * Accounting System database.
     *
     * @return The registered client has been turned into a response body.
     */
    public ClientResponseDto register(String id, String name, String email){

        var optionalClient = clientRepository.findByIdOptional(id);

        Client client;

        if(optionalClient.isPresent()){

            client = optionalClient.get();
            client.setEmail(email);
            client.setName(name);
        } else {
            client = new Client();

            client.setId(id);
            client.setName(name);
            client.setEmail(email);
            client.setCreatorId(id);
            client.setRegisteredOn(LocalDateTime.now());
            client.setRoles(Set.of("collection_reader"));
        }

        clientRepository.persistOrUpdate(client);

        return ClientMapper.INSTANCE.clientToResponse(client);
    }

    /**
     * Returns the N Clients from the given page.
     *
     * @param page Indicates the page number.
     * @param size The number of Clients to be retrieved.
     * @param uriInfo The current uri.
     * @return An object represents the paginated results.
     */
    public PageResource<ClientResponseDto> findAllClientsPageable(int page, int size, UriInfo uriInfo){

        var panacheQuery = clientRepository
                .findAll()
                .page(Page.of(page, size));

        return new PageResource<>(panacheQuery, ClientMapper.INSTANCE.clientsToResponse(panacheQuery.list()), uriInfo);
    }

    /**
     * This method assigns a list of Roles to a registered client.
     *
     * @param clientId The client unique identifier
     * @param roles The roles to be assigned to client
     */
    public ClientResponseDto assignRolesToRegisteredClient(String clientId, Set<String> roles){

        for(String role : roles){
            roleRepository.getRoleByName(role).orElseThrow(()->new NotFoundException("There is no Role with name : "+role));
        }

        var client = clientRepository.assignRolesToRegisteredClient(clientId, roles);

        return ClientMapper.INSTANCE.clientToResponse(client);
    }

    /**
     * This method detaches a list of Roles from a registered client.
     *
     * @param clientId The client unique identifier
     * @param roles The roles to be detached from a client
     */
    public ClientResponseDto detachRolesFromRegisteredClient(String clientId, Set<String> roles){

        for(String role : roles){
            roleRepository.getRoleByName(role).orElseThrow(()->new NotFoundException("There is no Role with name : "+role));
        }

        var client = clientRepository.detachRolesFromRegisteredClient(clientId, roles);

        return ClientMapper.INSTANCE.clientToResponse(client);
    }

    public Set<CollectionAccessPermissionDto> getGeneralPermissions(){

        var roleNames = clientRepository.getClientRoles(requestUserContext.getId());

        var roles = roleNames
                .stream()
                .map(roleName->roleService.getRoleByName(roleName))
                .collect(Collectors.toSet());

        return roleService.mergeRoles(roles);
    }
}
