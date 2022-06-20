package org.accounting.system.dtos.project;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Schema(name="AssociateProjectProviderRequest", description="Associates a list of Providers with a specific Project.")
public class AssociateProjectProviderRequestDto {

    @Schema(
            type = SchemaType.ARRAY,
            implementation = String.class,
            description = "This list must contain the Provider Ids to be associated with a specific Project.",
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
