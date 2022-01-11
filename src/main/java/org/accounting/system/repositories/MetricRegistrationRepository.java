package org.accounting.system.repositories;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.accounting.system.entities.MetricRegistration;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class MetricRegistrationRepository implements PanacheMongoRepository<MetricRegistration> {

    public Optional<MetricRegistration> exist(String unitType, String name){

        return find("unitType = ?1 and metricName = ?2", unitType.toLowerCase(), name.toLowerCase()).stream().findAny();
    }


}
