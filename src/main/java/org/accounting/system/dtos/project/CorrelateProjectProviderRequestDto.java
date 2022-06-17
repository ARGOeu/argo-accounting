package org.accounting.system.dtos.project;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Schema(name="CorrelateProjectProviderRequest", description="Correlates a list of Providers with a specific Project.")
public class CorrelateProjectProviderRequestDto {

    @Schema(
            type = SchemaType.ARRAY,
            implementation = String.class,
            description = "This list must contain the Provider Ids to be correlated with a specific Project.",
            minItems = 1,
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
    @NotEmpty(message = "providers should have at least one entry.")
    public Set<String> providers;
}
