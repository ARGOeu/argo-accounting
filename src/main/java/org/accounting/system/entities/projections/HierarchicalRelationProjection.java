package org.accounting.system.entities.projections;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.accounting.system.HierarchicalRelationSerializer;
import org.accounting.system.enums.RelationType;
import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@JsonSerialize(using = HierarchicalRelationSerializer.class)
public class HierarchicalRelationProjection {

    public String id;

    public String externalId;

    @BsonProperty(value = "full_metrics")
    public Set<Document> metrics = new HashSet<>();

    public RelationType relationType;

    public Collection<HierarchicalRelationProjection> hierarchicalRelations = new HashSet<>();
}