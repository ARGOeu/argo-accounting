package org.accounting.system.dtos.project;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Set;

@Schema(name="DissociateProjectProviderRequest", description="Dissociates a list of Providers from a specific Project.")
public class DissociateProjectProviderRequestDto {

    @Schema(
            type = SchemaType.ARRAY,
            implementation = String.class,
            description = "This list must contain the Provider Ids to be dissociated from a specific Project.",
            minItems = 1,
            example = "[\n" +
                    "      \"grnet\",\n" +
                    "      \"osmooc\",\n" +
                    "      \"sites\"\n" +
                    "   ]"
    )
    @JsonProperty("providers")
    @NotEmpty(message = "providers should have at least one entry.")
    public Set<String> providers;
}
