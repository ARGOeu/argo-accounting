package org.accounting.system;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.accounting.system.entities.projections.HierarchicalRelationProjection;
import org.accounting.system.enums.RelationType;
import org.bson.Document;

import java.io.IOException;

public class HierarchicalRelationSerializer extends JsonSerializer<HierarchicalRelationProjection> {

    @Override
    public void serialize(HierarchicalRelationProjection hierarchicalRelationProjection, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

        if(hierarchicalRelationProjection.relationType.equals(RelationType.PROJECT)){
            jsonGenerator.writeStartObject();
            write(hierarchicalRelationProjection, jsonGenerator);
            jsonGenerator.writeEndObject();
        }
    }

    private void write(HierarchicalRelationProjection hierarchicalRelationProjection, JsonGenerator jsonGenerator) throws IOException{

        if(hierarchicalRelationProjection.relationType.equals(RelationType.PROJECT)){
            jsonGenerator.writeStringField("project", hierarchicalRelationProjection.externalId);
            jsonGenerator.writeArrayFieldStart("providers");
        } else if (hierarchicalRelationProjection.relationType.equals(RelationType.PROVIDER)){
            jsonGenerator.writeStringField("provider", hierarchicalRelationProjection.externalId);
            jsonGenerator.writeArrayFieldStart("installations");
        } else {
            jsonGenerator.writeStringField("installation", hierarchicalRelationProjection.externalId);
        }

        if(!hierarchicalRelationProjection.metrics.isEmpty()){
            jsonGenerator.writeArrayFieldStart("metrics");
            jsonGenerator.writeStartArray();
            for (Document document: hierarchicalRelationProjection.metrics) {
                jsonGenerator.writeObject(document);
            }
            jsonGenerator.writeEndArray();
        }

        for(HierarchicalRelationProjection projection : hierarchicalRelationProjection.hierarchicalRelations){
            jsonGenerator.writeStartObject();
            write(projection, jsonGenerator);
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
    }
}

