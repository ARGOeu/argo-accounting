package org.accounting.system.services.client;

import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.oidc.TokenIntrospection;
import io.quarkus.security.spi.runtime.AuthorizationController;
import org.accounting.system.dtos.client.ClientResponseDto;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.entities.client.Client;
import org.accounting.system.mappers.ClientMapper;
import org.accounting.system.repositories.client.ClientRepository;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.UriInfo;
import java.util.Objects;

@ApplicationScoped
public class ClientService {

    @Inject
    ClientRepository clientRepository;

    @Inject
    TokenIntrospection tokenIntrospection;

    @Inject
    AuthorizationController authorizationController;

    @ConfigProperty(name = "key.to.retrieve.id.from.access.token")
    String key;



    /**
     * This method extracts specific information regarding the client from access token and stores it into the
     * Accounting System database.
     *
     * @return The registered client has been turned into a response body.
     */
    public ClientResponseDto register(){


        if(Objects.isNull(tokenIntrospection.getJsonObject())){
            throw new ForbiddenException("The authentication process couldn't be performed, so the client registration is not possible.");
        }

        String id = tokenIntrospection.getJsonObject().getString(key);
        String name = Objects.isNull(tokenIntrospection.getJsonObject().get("name")) ? "": tokenIntrospection.getJsonObject().getString("name");
        String email = Objects.isNull(tokenIntrospection.getJsonObject().get("email")) ? "": tokenIntrospection.getJsonObject().getString("email");

        Client client = new Client();

        client.setId(id);
        client.setName(name);
        client.setEmail(email);
        client.setCreatorId(id);

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
    public PageResource<Client, ClientResponseDto> findAllClientsPageable(int page, int size, UriInfo uriInfo){

        PanacheQuery<Client> panacheQuery = clientRepository.findAllPageable(page, size);

        return new PageResource<>(panacheQuery, ClientMapper.INSTANCE.clientsToResponse(panacheQuery.list()), uriInfo);
    }
}
