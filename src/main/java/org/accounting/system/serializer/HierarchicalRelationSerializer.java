package org.accounting.system.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.accounting.system.entities.projections.HierarchicalRelationProjection;
import org.accounting.system.enums.RelationType;

import java.io.IOException;

public class HierarchicalRelationSerializer extends StdSerializer<HierarchicalRelationProjection> {

    public HierarchicalRelationSerializer() {
        super(HierarchicalRelationProjection.class);
    }

    public HierarchicalRelationSerializer(Class<HierarchicalRelationProjection> t) {
        super(t);
    }

    @Override
    public void serialize(HierarchicalRelationProjection hierarchicalRelationProjection, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

            jsonGenerator.writeStartObject();
            write(hierarchicalRelationProjection, jsonGenerator);
            jsonGenerator.writeEndObject();
    }

    private void write(HierarchicalRelationProjection hierarchicalRelationProjection, JsonGenerator jsonGenerator) throws IOException{

        if(hierarchicalRelationProjection.relationType.equals(RelationType.PROJECT)){
            jsonGenerator.writeStringField("project_id", hierarchicalRelationProjection.externalId);
            jsonGenerator.writeStringField("acronym", hierarchicalRelationProjection.project().getAcronym());
            jsonGenerator.writeStringField("title", hierarchicalRelationProjection.project().getTitle());

            jsonGenerator.writeArrayFieldStart("providers");
        } else if (hierarchicalRelationProjection.relationType.equals(RelationType.PROVIDER)){
            jsonGenerator.writeStringField("provider_id", hierarchicalRelationProjection.externalId);
            jsonGenerator.writeStringField("name", hierarchicalRelationProjection.provider().getName());
            jsonGenerator.writeArrayFieldStart("installations");
        }

//        if(!hierarchicalRelationProjection.metrics.isEmpty()){
//            jsonGenerator.writeArrayFieldStart("metrics");
//            jsonGenerator.writeStartArray();
//            for (Document document: hierarchicalRelationProjection.metrics) {
//                jsonGenerator.writeObject(document);
//            }
//            jsonGenerator.writeEndArray();
//        }

        for(HierarchicalRelationProjection projection : hierarchicalRelationProjection.hierarchicalRelations){

            if(projection.relationType.equals(RelationType.INSTALLATION)){
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("installation_id", projection.externalId);
                jsonGenerator.writeStringField("installation", projection.installation().getInstallation());
                jsonGenerator.writeStringField("infrastructure", projection.installation().getInfrastructure());
                jsonGenerator.writeEndObject();
            } else {
                jsonGenerator.writeStartObject();
                write(projection, jsonGenerator);
                jsonGenerator.writeEndObject();
            }
        }
        jsonGenerator.writeEndArray();
    }
}