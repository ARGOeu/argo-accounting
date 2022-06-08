package org.accounting.system.dtos.project;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Schema(name="CorrelateProjectProviderRequest", description="Correlates a list of Providers with a specific Project.")
public class CorrelateProjectProviderRequestDto {

    @Schema(
            type = SchemaType.ARRAY,
            implementation = String.class,
            description = "This list must contain the Provider Ids to be correlated with a specific Project.",
            example = "{\n" +
                    " \"providers\":\n" +
                    "    [\n" +
                    "   \"osmooc\",\n" +
                    "   \"grnet\",\n" +
                    "   \"sites\"\n" +
                    "    ]\n" +
                    "}"
    )
    @JsonProperty("providers")
    public List<String> providers;
}
