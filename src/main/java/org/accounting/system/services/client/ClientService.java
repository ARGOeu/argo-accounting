package org.accounting.system.services.client;

import io.quarkus.mongodb.panache.PanacheQuery;
import org.accounting.system.dtos.client.ClientResponseDto;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.entities.client.Client;
import org.accounting.system.mappers.ClientMapper;
import org.accounting.system.repositories.authorization.RoleRepository;
import org.accounting.system.repositories.client.ClientRepository;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public class ClientService {

    @Inject
    ClientRepository clientRepository;

    @Inject
    RoleRepository roleRepository;

    /**
     * This method extracts specific information regarding the client from access token and stores it into the
     * Accounting System database.
     *
     * @return The registered client has been turned into a response body.
     */
    public ClientResponseDto register(String id, String name, String email){

        if(StringUtils.isEmpty(id)){
            throw new ForbiddenException("voperson_id is empty. The client cannot be registered without voperson_id.");
        }

        Optional<Client> optionalClient = clientRepository.findByIdOptional(id);

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
            client.setRoles(Set.of("collection_creator"));
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

        PanacheQuery<Client> panacheQuery = clientRepository.findAllPageable(page, size);

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
}
