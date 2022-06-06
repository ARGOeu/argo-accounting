package org.accounting.system.repositories;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import org.accounting.system.entities.HierarchicalRelation;
import org.accounting.system.entities.projections.HierarchicalRelationProjection;
import org.accounting.system.entities.projections.MetricProjection;
import org.accounting.system.entities.projections.ProjectionQuery;
import org.accounting.system.repositories.metric.MetricRepository;
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
import java.util.stream.Collectors;

@ApplicationScoped
public class HierarchicalRelationRepository implements PanacheMongoRepositoryBase<HierarchicalRelation, String> {

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
     * This method executes a query to check if the given Provider belongs to a specific Project.
     *
     * @param projectId The Project ID.
     * @param providerId The Provider ID.
     * @return if provider belongs to project.
     */
    public boolean providerBelongsToProject(String projectId, String providerId){

        Bson regex = Aggregates.match(Filters.regex("_id", projectId + HierarchicalRelation.PATH_SEPARATOR + providerId));

        Document count = getMongoCollection()
                .aggregate(List
                        .of(regex, Aggregates.count())).first();

        return count != null && Long.parseLong(count.get("count").toString()) > 0L;
    }

    public List<HierarchicalRelation> findAllByExternalId(String id){
        return find("externalId = ?1", id).stream().collect(Collectors.toList());
    }

    public HierarchicalRelation findByPath(final String id){

        var document = findById(id);

        if( document == null ) {
            return document;
        }

        return build(document, find(Document.parse(Filters.regex("_id", id + "[.\\s]").toBsonDocument().toJson())).list());
    }

    public ProjectionQuery<MetricProjection> findByExternalId(final String externalId, int page, int size) {
        //Bson regex = Aggregates.match(Filters.regex("resource_id", externalId + "[.\\s]"));
        Bson regex = Aggregates.match(Filters.regex("resource_id", externalId));

        List<MetricProjection> projections = metricRepository
                .getMongoCollection()
                .aggregate(List
                        .of(regex,  Aggregates.skip(size * (page)),Aggregates.limit(size)), MetricProjection.class).into(new ArrayList<>());

        Document count = metricRepository
                .getMongoCollection()
                .aggregate(List
                        .of(regex, Aggregates.count())).first();

        var projectionQuery = new ProjectionQuery<MetricProjection>();

        projectionQuery.list = projections;
        projectionQuery.index = page;
        projectionQuery.size = size;
        projectionQuery.count = count == null ? 0L : Long.parseLong(count.get("count").toString());

        return projectionQuery;
    }

//    public List<HierarchicalRelationProjection> findByExternalId(final String externalId, int page, int size, UriInfo uriInfo) {
//
//        Document externalIdToInstallation = Document.parse("{\n" +
//                "        $cond: [\n" +
//                "          {\n" +
//                "            $eq: [\n" +
//                "              \"$relationType\",\n" +
//                "              \"INSTALLATION\"\n" +
//                "            ]\n" +
//                "          },\n" +
//                "          {\n" +
//                "            \"$toObjectId\": \"$externalId\"\n" +
//                "          },\n" +
//                "          \"$$REMOVE\"\n" +
//                "        ]\n" +
//                "      }");
//
//        Document externalIdToProject = Document.parse("{\n" +
//                "        $cond: [\n" +
//                "          {\n" +
//                "            $eq: [\n" +
//                "              \"$relationType\",\n" +
//                "              \"PROJECT\"\n" +
//                "            ]\n" +
//                "          },\n" +
//                "          \"$externalId\",\n" +
//                "          \"$$REMOVE\"\n" +
//                "        ]\n" +
//                "      }");
//
//        Document externalIdToProvider = Document.parse("{\n" +
//                "        $cond: [\n" +
//                "          {\n" +
//                "            $eq: [\n" +
//                "              \"$relationType\",\n" +
//                "              \"PROVIDER\"\n" +
//                "            ]\n" +
//                "          },\n" +
//                "          \"$externalId\",\n" +
//                "          \"$$REMOVE\"\n" +
//                "        ]\n" +
//                "      }");
//
//        Document removeInstallationFunction = Document.parse("{\n" +
//                "        $cond: [\n" +
//                "          {\n" +
//                "            $eq: [\n" +
//                "              {\n" +
//                "                $size: \"$installation\"\n" +
//                "              },\n" +
//                "              0\n" +
//                "            ]\n" +
//                "          },\n" +
//                "          \"$$REMOVE\",\n" +
//                "          \"$installation\"\n" +
//                "        ]\n" +
//                "      }");
//
//
//        Field installation = new Field<>("installation", externalIdToInstallation);
//        Field provider = new Field<>("provider", externalIdToProvider);
//        Field project = new Field<>("project", externalIdToProject);
//
//        Field remove = new Field<>("installation", removeInstallationFunction);
//
//        Bson fields = Aggregates.addFields(installation, provider, project);
//
//        Bson lookupMetric = Aggregates.lookup("Metric", "metrics", "_id", "full_metrics");
//
//        Bson lookupInstallation = Aggregates.lookup("Installation", "installation", "_id", "installation");
//
//        Bson regex = Aggregates.match(Filters.regex("_id", externalId + "[.\\s]"));
//
//        Bson documentsByExternalId = Aggregates.match(Filters.eq("_id", externalId));
//
//        final var documents = getMongoCollection().aggregate(List.of(fields, lookupMetric, lookupInstallation, documentsByExternalId, Aggregates.addFields(remove)), HierarchicalRelationProjection.class).into(new ArrayList<>());
//
//        if( documents == null ) {
//            return documents;
//        }
//
//        return documents.
//                stream()
//                .map(document->build(document, getMongoCollection().aggregate(List.of(fields, lookupMetric, lookupInstallation, regex, Aggregates.addFields(remove)), HierarchicalRelationProjection.class).into(new ArrayList<>())))
//                .collect(Collectors.toList());
//    }

    private HierarchicalRelationProjection build(final HierarchicalRelationProjection root, final Collection<HierarchicalRelationProjection> documents ) {
        final Map<String, HierarchicalRelationProjection> map = new HashMap<>();

        for(final HierarchicalRelationProjection document: documents) {
            map.put(document.id, document);
        }

        for(final HierarchicalRelationProjection document: documents) {
            map.put(document.id, document);

            final String path = document.id
                    .substring(0, document.id.lastIndexOf(HierarchicalRelation.PATH_SEPARATOR));

            if(path.equals(root.id)){
                root.hierarchicalRelations.add(document);
            } else{
                final HierarchicalRelationProjection parent = map.get(path);
                if(parent != null ) {
                    parent.hierarchicalRelations.add(document);
                }
            }
        }

        return root;
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
