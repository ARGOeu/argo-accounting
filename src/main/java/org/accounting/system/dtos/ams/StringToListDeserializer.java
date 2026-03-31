package org.accounting.system.dtos.ams;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.List;

public class StringToListDeserializer extends JsonDeserializer<List<String>> {
    @Override
    public List<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

        if (p.currentToken().isScalarValue()) {
            return List.of(p.getValueAsString());
        }
        return p.readValueAs(new TypeReference<List<String>>() {});
    }
}
