package org.accounting.system.repositories;

import com.mongodb.client.model.Collation;
import com.mongodb.client.model.CollationStrength;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.accounting.system.entities.OidcTenantConfig;
import org.bson.types.ObjectId;

import java.util.Optional;

@ApplicationScoped
public class OidcTenantConfigRepository implements PanacheMongoRepositoryBase<OidcTenantConfig, ObjectId> {

    public Optional<OidcTenantConfig> fetchOidcTenantConfigByTenant(String tenantId){

        return find("tenantId = ?1", tenantId)
                .withCollation(Collation.builder().locale("en").collationStrength(CollationStrength.SECONDARY).build())
                .stream()
                .findAny();
    }

    public Optional<OidcTenantConfig> fetchOidcTenantConfigByIssuer(String issuer){

        return find("issuer = ?1", issuer)
                .withCollation(Collation.builder().locale("en").collationStrength(CollationStrength.SECONDARY).build())
                .stream()
                .findAny();
    }
}
