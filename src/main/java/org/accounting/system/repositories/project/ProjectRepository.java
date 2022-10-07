package org.accounting.system.repositories.project;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UnwindOptions;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.oidc.TokenIntrospection;
import io.quarkus.panache.common.Page;
import org.accounting.system.entities.Project;
import org.accounting.system.entities.projections.MongoQuery;
import org.accounting.system.enums.AccessType;
import org.accounting.system.enums.Collection;
import org.accounting.system.enums.Operation;
import org.accounting.system.repositories.acl.AccessControlRepository;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ProjectRepository extends ProjectModulator {

    @Inject
    AccessControlRepository accessControlRepository;

    @Inject
    TokenIntrospection tokenIntrospection;

    @ConfigProperty(name = "key.to.retrieve.id.from.access.token")
    String id;

    public PanacheQuery<Project> fetchAll(int page, int size) {

        Bson lookupProjects = Aggregates.lookup("Project", "entity", "_id", "project");

        Bson lookupRoles = Aggregates.lookup("Role", "roles", "name", "roles");

        Bson eqAccessControl = Aggregates.match(Filters.and(Filters.eq("who", tokenIntrospection.getJsonObject().getString(id)), Filters.eq("collection", Collection.Project.name())));

        Bson eqCollection = Aggregates.match(Filters.eq("roles.collections_access_permissions.collection", Collection.Project.name()));
        Bson eqOperation = Aggregates.match(Filters.eq("roles.collections_access_permissions.access_permissions.operation", Operation.READ.name()));
        Bson eqAccessType = Aggregates.match(Filters.eq("roles.collections_access_permissions.access_permissions.access_type", AccessType.ALWAYS.name()));

        var unwindOptions = new UnwindOptions();

        var unwindProject = Aggregates.unwind("$project", unwindOptions.preserveNullAndEmptyArrays(Boolean.FALSE));

        var unwindRoles = Aggregates.unwind("$roles");
        var unwindCap = Aggregates.unwind("$roles.collections_access_permissions");
        var unwindCapa = Aggregates.unwind("$roles.collections_access_permissions.access_permissions");

        var project = Aggregates.project(Projections.fields(Projections.include("project"), Projections.excludeId()));

        var newRoot = Aggregates.replaceRoot("$project");

        var projects = accessControlRepository
                .getMongoCollection()
                .aggregate(List.of(eqAccessControl, lookupProjects, unwindProject, lookupRoles, unwindRoles, unwindCap, unwindCapa, eqCollection, eqOperation, eqAccessType, Aggregates.skip(size * (page)), Aggregates.limit(size), project, newRoot), Project.class)
                .into(new ArrayList<>());

        Document count = accessControlRepository
                .getMongoCollection()
                .aggregate(List.of(eqAccessControl, lookupProjects, unwindProject, lookupRoles, unwindRoles, unwindCap, unwindCapa, eqCollection, eqOperation, eqAccessType, Aggregates.count()))
                .first();

        var projectionQuery = new MongoQuery<Project>();

        projectionQuery.list = projects;
        projectionQuery.index = page;
        projectionQuery.size = size;
        projectionQuery.count = count == null ? 0L : Long.parseLong(count.get("count").toString());
        projectionQuery.page = Page.of(page, size);

        return projectionQuery;
    }
}
