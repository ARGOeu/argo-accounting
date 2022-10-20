package org.accounting.system.repositories;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import org.accounting.system.entities.HierarchicalRelation;
import org.accounting.system.entities.projections.MetricProjection;
import org.accounting.system.entities.projections.MongoQuery;
import org.accounting.system.repositories.metric.MetricRepository;
import org.accounting.system.repositories.modulators.AbstractAccessModulator;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@ApplicationScoped
public class HierarchicalRelationRepository extends AbstractAccessModulator<HierarchicalRelation, String> {

    @Inject
    MongoClient mongoClient;

    @Inject
    MetricRepository metricRepository;

    public void save(HierarchicalRelation hierarchicalRelation, ObjectId metric){

        var toBeStored = findByPath(hierarchicalRelation.id);

        if(Objects.isNull(toBeStored)){

            CollectionUtils.addIgnoreNull(hierarchicalRelation.metrics, metric);
            persist(hierarchicalRelation);
        } else {
            CollectionUtils.addIgnoreNull(toBeStored.metrics, metric);
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

    public PanacheQuery<MetricProjection> findByExternalId(final String externalId, int page, int size) {
        //Bson regex = Aggregates.match(Filters.regex("resource_id", externalId + "[.\\s]"));
        Bson regex = Aggregates.match(Filters.regex("resource_id", "\\b" + externalId + "\\b"+"(?![-])"));

        List<MetricProjection> projections = metricRepository
                .getMongoCollection()
                .aggregate(List
                        .of(regex,  Aggregates.skip(size * (page)),Aggregates.limit(size)), MetricProjection.class).into(new ArrayList<>());

        Document count = metricRepository
                .getMongoCollection()
                .aggregate(List
                        .of(regex, Aggregates.count())).first();

        var projectionQuery = new MongoQuery<MetricProjection>();

        projectionQuery.list = projections;
        projectionQuery.index = page;
        projectionQuery.size = size;
        projectionQuery.count = count == null ? 0L : Long.parseLong(count.get("count").toString());
        projectionQuery.page = Page.of(page, size);

        return projectionQuery;
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

    public MongoCollection<Document> getMongoCollection(){
        return mongoClient.getDatabase("accounting-system").getCollection("HierarchicalRelation");
    }
}