package org.accounting.system.entities.projections;

import org.accounting.system.entities.Project;
import org.accounting.system.entities.installation.Installation;
import org.accounting.system.entities.provider.Provider;
import org.accounting.system.enums.RelationType;
import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class HierarchicalRelationProjection {

    public String id;

    public String externalId;

    @BsonProperty(value = "full_metrics")
    public Set<Document> metrics = new HashSet<>();

    public RelationType relationType;

    public List<Installation> installation;

    public List<Project> project;

    public List<Provider> provider;

    public Collection<HierarchicalRelationProjection> hierarchicalRelations = new TreeSet<>(Comparator.comparing(t -> t.externalId));

    public Installation installation(){
        return installation.get(0);
    }

    public Project project(){
        return project.get(0);
    }

    public Provider provider(){
        return provider.get(0);
    }

}