package org.accounting.system.entities;

import org.accounting.system.enums.RelationType;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import java.util.Collection;
import java.util.HashSet;

public class HierarchicalRelation extends Entity {

    public static final String PATH_SEPARATOR = ".";

    @BsonId
    public String id;

    public String externalId;

    public RelationType relationType;

    public String externalUniqueIdentifier;

    @BsonIgnore
    public Collection<HierarchicalRelation> hierarchicalRelations = new HashSet<>();

    public HierarchicalRelation() {
    }

    public HierarchicalRelation(final String externalId, final RelationType relationType) {
        this.externalId = externalId;
        this.id = externalId;
        this.relationType = relationType;
    }

    public HierarchicalRelation(final String externalId, final HierarchicalRelation hierarchicalRelation, final RelationType relationType, final String externalUniqueIdentifier) {
        this.externalId = externalId;
        this.id = hierarchicalRelation.id + PATH_SEPARATOR + externalId;
        this.relationType = relationType;
        this.externalUniqueIdentifier = externalUniqueIdentifier;
    }
}