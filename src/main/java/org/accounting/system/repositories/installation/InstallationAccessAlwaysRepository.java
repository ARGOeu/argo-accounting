package org.accounting.system.repositories.installation;

import io.quarkus.mongodb.panache.PanacheQuery;
import org.accounting.system.dtos.installation.InstallationRequestDto;
import org.accounting.system.entities.HierarchicalRelation;
import org.accounting.system.entities.acl.RoleAccessControl;
import org.accounting.system.entities.installation.Installation;
import org.accounting.system.entities.projections.MetricProjection;
import org.accounting.system.enums.RelationType;
import org.accounting.system.mappers.InstallationMapper;
import org.accounting.system.repositories.HierarchicalRelationRepository;
import org.accounting.system.repositories.modulators.AccessAlwaysModulator;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class InstallationAccessAlwaysRepository extends AccessAlwaysModulator<Installation, ObjectId, RoleAccessControl> {


    @Inject
    HierarchicalRelationRepository hierarchicalRelationRepository;

    public Installation save(InstallationRequestDto request) {

        var installationToBeStored = InstallationMapper.INSTANCE.requestToInstallation(request);

        persist(installationToBeStored);

        HierarchicalRelation project = new HierarchicalRelation(request.project, RelationType.PROJECT);

        HierarchicalRelation provider = new HierarchicalRelation(request.organisation, project, RelationType.PROVIDER);

        HierarchicalRelation installation = new HierarchicalRelation(installationToBeStored.getId().toString(), provider, RelationType.INSTALLATION);

        hierarchicalRelationRepository.save(project, null);
        hierarchicalRelationRepository.save(provider, null);
        hierarchicalRelationRepository.save(installation, null);

        return installationToBeStored;
    }

    public PanacheQuery<MetricProjection> fetchAllMetrics(String id, int page, int size){

        return hierarchicalRelationRepository.findByExternalId(id, page, size);
    }

    public boolean accessibility(String project, String provider, String installation){

        return true;
    }
}
