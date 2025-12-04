package org.accounting.system.services;

import jakarta.enterprise.context.ApplicationScoped;
import org.accounting.system.dtos.permissions.AccessPermissionDto;
import org.accounting.system.dtos.permissions.CollectionAccessPermissionDto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class ClientService {

    public List<CollectionAccessPermissionDto> getGeneralPermissions(){

        var  list = new ArrayList<CollectionAccessPermissionDto>();

        var metricDefinition = new CollectionAccessPermissionDto();

        metricDefinition.accessPermissions = access();
        metricDefinition.collection = "MetricDefinition";
        list.add(metricDefinition);

        var ut = new CollectionAccessPermissionDto();

        ut.accessPermissions = access();
        ut.collection = "UnitType";
        list.add(ut);

        var mt = new CollectionAccessPermissionDto();

        mt.accessPermissions = access();
        mt.collection = "MetricType";
        list.add(mt);

        var cl = new CollectionAccessPermissionDto();

        cl.accessPermissions = access();
        cl.collection = "Client";
        list.add(cl);

        var pr = new CollectionAccessPermissionDto();

        pr.accessPermissions = access();
        pr.collection = "Provider";
        list.add(pr);

        return list;
    }

    private Set<AccessPermissionDto> access(){

        var read = new AccessPermissionDto();
        read.accessType = "ALWAYS";
        read.operation = "READ";

        var create = new AccessPermissionDto();
        create.accessType = "ALWAYS";
        create.operation = "CREATE";

        var update = new AccessPermissionDto();
        update.accessType = "ALWAYS";
        update.operation = "UPDATE";

        var delete = new AccessPermissionDto();
        delete.accessType = "ALWAYS";
        delete.operation = "DELETE";

        return Set.of(read, create, update, delete);
    }


    public Set<CollectionAccessPermissionDto> projectAdmin(){

        var  list = new HashSet<CollectionAccessPermissionDto>();

        var project = new CollectionAccessPermissionDto();

        project.accessPermissions = project();
        project.collection = "Project";
        list.add(project);

        var metric = new CollectionAccessPermissionDto();

        metric.accessPermissions = access();
        metric.collection = "Metric";
        list.add(metric);

        var installation = new CollectionAccessPermissionDto();

        installation.accessPermissions = access();
        installation.collection = "Installation";
        list.add(installation);

        return list;
    }

    private Set<AccessPermissionDto> project(){

        var associate = new AccessPermissionDto();
        associate.accessType = "ALWAYS";
        associate.operation = "ASSOCIATE";

        var dissociate = new AccessPermissionDto();
        dissociate.accessType = "ALWAYS";
        dissociate.operation = "DISSOCIATE";

        var read = new AccessPermissionDto();
        read.accessType = "ALWAYS";
        read.operation = "READ";

        return Set.of(associate, dissociate, read);
    }

}
