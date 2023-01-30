package org.accounting.system.repositories;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import org.accounting.system.entities.HierarchicalRelation;
import org.accounting.system.repositories.modulators.AbstractAccessModulator;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@ApplicationScoped
public class HierarchicalRelationRepository extends AbstractAccessModulator<HierarchicalRelation, String> {

    public void save(HierarchicalRelation hierarchicalRelation){

        var toBeStored = findByPath(hierarchicalRelation.id);

        if(Objects.isNull(toBeStored)){

            persist(hierarchicalRelation);
        } else {
            persistOrUpdate(toBeStored);
        }
    }

    /**
     * This method executes a query to check if the given hierarchical relationship exists
     *
     * @param path Hierarchical relationship path
     * @return if the given path exists.
     */
    public boolean exist(String path){

        Bson regex = Aggregates.match(Filters.regex("_id","\\b" + path + "\\b"+"(?![-])"));

        Document count = getMongoCollection()
                .aggregate(List
                        .of(regex, Aggregates.count())).first();

        return count != null && Long.parseLong(count.get("count").toString()) > 0L;
    }


    public HierarchicalRelation findByPath(final String id) {

        var document = findById(id);

        if (document == null) {
            return document;
        }

        return build(document, find(Document.parse(Filters.regex("_id", id + "[.\\s]").toBsonDocument().toJson())).list());
    }

    private HierarchicalRelation build(final HierarchicalRelation root, final Collection<HierarchicalRelation> documents ) {
        final Map<String, HierarchicalRelation> map = new HashMap<>();

        for(final HierarchicalRelation document: documents) {
            map.put(document.id, document);
        }

        for(final HierarchicalRelation document: documents) {
            map.put(document.id, document);

            final String path = document.id
                    .substring(0, document.id.lastIndexOf(HierarchicalRelation.PATH_SEPARATOR));

            if(path.equals(root.id)){
                root.hierarchicalRelations.add(document);
            } else{
                final HierarchicalRelation parent = map.get(path);
                if(parent != null ) {
                    parent.hierarchicalRelations.add(document);
                }
            }
        }

        return root;
    }
}