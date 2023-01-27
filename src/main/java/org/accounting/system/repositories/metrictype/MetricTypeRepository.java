package org.accounting.system.repositories.metrictype;

import com.mongodb.client.model.Collation;
import com.mongodb.client.model.CollationStrength;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.accounting.system.dtos.metrictype.UpdateMetricTypeRequestDto;
import org.accounting.system.entities.MetricType;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.mappers.MetricTypeMapper;
import org.accounting.system.repositories.metricdefinition.MetricDefinitionRepository;
import org.accounting.system.repositories.modulators.AccessibleModulator;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import java.util.Optional;

/**
 * {@link MetricTypeRepository This repository} encapsulates the logic required to access
 * {@link MetricType} data stored in the mongo database. More specifically, it encapsulates the queries
 * that can be performed on the {@link MetricType} collection. It is also responsible for mapping
 * the data from the storage format to the {@link MetricType}.
 *
 * Since {@link MetricTypeRepository this repository} extends {@link AccessibleModulator},
 * it has access to all queries, which determine the degree of accessibility of the data.
 *
 * Also, all the operations that are defined on {@link PanacheMongoRepository} are available on this repository.
 * In this repository, we essentially define the queries that will be executed on the database without any restrictions.
 */
@ApplicationScoped
public class MetricTypeRepository extends AccessibleModulator<MetricType, ObjectId> {

    @Inject
    MetricDefinitionRepository metricDefinitionRepository;

    /**
     * This method fetches an entity from the database by given metric type.
     *
     * @param metricType The Metric Type that has been requested.
     * @return The wrapped Metric Type as Optional.
     */
    public Optional<MetricType> fetchMetricByType(String metricType){

        return find("metricType = ?1", metricType)
                .withCollation(Collation.builder().locale("en")
                        .collationStrength(CollationStrength.SECONDARY).build())
                .stream().findAny();
    }

    /**
     * This method is responsible for updating a part or all attributes of an existing Metric Type.
     *
     * @param id The Metric Type to be updated.
     * @param request The Metric Type attributes that have been requested to be updated.
     * @throws ForbiddenException If the editing action is not allowed
     * @return The updated Metric Type.
     */
    public MetricType updateEntity(ObjectId id, UpdateMetricTypeRequestDto request) {

        MetricType entity = findById(id);

        if(StringUtils.isEmpty(entity.getCreatorId())){
            throw new ForbiddenException("You cannot update a Metric Type registered by Accounting Service.");
        }

        var isUsed = metricDefinitionRepository.metricTypeUsedInMetricDefinition(entity.getMetricType());

        if(isUsed){
            throw new ForbiddenException("This Metric Type is used in an existing Metric Definition, so you cannot update it.");
        }

        if(StringUtils.isNotEmpty(request.metricType)){
            var optional = fetchMetricByType(request.metricType);

            optional.ifPresent(ut -> {throw new ConflictException("This Metric Type already exists in the Accounting Service database. Its id is "+ut.getId().toString());});
        }

        MetricTypeMapper.INSTANCE.updateMetricTypeFromDto(request, entity);

        return super.updateEntity(entity, id);
    }
}