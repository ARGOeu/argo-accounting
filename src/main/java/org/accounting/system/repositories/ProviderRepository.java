package org.accounting.system.repositories;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import org.accounting.system.entities.Provider;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProviderRepository implements PanacheMongoRepositoryBase<Provider, String> {

    /**
     * Executes a query in mongo database and returns the paginated results..
     * The page parameter indicates the requested page number, and the size parameter the number of entities by page.
     *
     * @param page The page to be retrieved
     * @param size The requested size of page
     * @return An object represents the paginated results
     */
    public PanacheQuery<Provider> findAllProvidersPageable(int page, int size, String id){

        if(StringUtils.isEmpty(id)){
            return findAll().page(Page.of(page, size));
        } else {
            return find("_id = ?1", id).page(Page.of(page, size));
        }
    }
}
