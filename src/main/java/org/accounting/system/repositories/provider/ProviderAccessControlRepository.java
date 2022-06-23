package org.accounting.system.repositories.provider;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import org.accounting.system.entities.HierarchicalRelation;
import org.accounting.system.entities.acl.AccessControl;
import org.accounting.system.entities.projections.InstallationProjection;
import org.accounting.system.entities.projections.ProjectionQuery;
import org.accounting.system.entities.provider.Provider;
import org.accounting.system.enums.acl.AccessControlPermission;
import org.accounting.system.repositories.modulators.AccessControlModulator;
import org.bson.conversions.Bson;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ForbiddenException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProviderAccessControlRepository extends AccessControlModulator<Provider, String> {


    public boolean accessibility(String projectId, String providerId){

        var optional = getAccessControl(projectId + HierarchicalRelation.PATH_SEPARATOR + providerId, AccessControlPermission.ACCESS_PROVIDER);

        if(optional.isPresent()){
            return true;
        } else {
            throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
        }
    }

    public ProjectionQuery<InstallationProjection> lookupInstallations(String from, String localField, String foreignField, String as, int page, int size, Class<InstallationProjection> projection) {

        var providers = getAccessControlRepository().findAllByWhoAndCollection(getRequestInformation().getSubjectOfToken(), collection(), AccessControlPermission.ACCESS_PROVIDER);

        var projectsToString = providers
                .stream()
                .map(AccessControl::getEntity)
                .collect(Collectors.toList());

        Bson bson = Aggregates.lookup(from, localField, foreignField, as);

        List<InstallationProjection> projections = getMongoCollection("Installation").aggregate(List.of(bson, Aggregates.skip(size * (page)), Aggregates.match(Filters.in("project", projectsToString)), Aggregates.limit(size)), projection).into(new ArrayList<>());

        var projectionQuery = new ProjectionQuery<InstallationProjection>();

        projectionQuery.list = projections;
        projectionQuery.index = page;
        projectionQuery.size = size;
        projectionQuery.count = getMongoCollection().countDocuments();

        return projectionQuery;
    }
}
