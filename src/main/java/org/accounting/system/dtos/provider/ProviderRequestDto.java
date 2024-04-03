package org.accounting.system.dtos.provider;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="ProviderRequest", description="An object represents a request for creating a Provider.")
public class ProviderRequestDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Provider ID.",
            example = "sites",
            required = true
    )
    @JsonProperty("id")
    @NotEmpty(message = "id may not be empty.")
    public String id;
    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Provider name.",
            example = "Swedish Infrastructure for Ecosystem Science",
            required = true
    )
    @JsonProperty("name")
    @NotEmpty(message = "name may not be empty.")
    public String name;
    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Provider website.",
            example = "https://www.fieldsites.se/en-GB"
    )
    @JsonProperty("website")
    public String website;
    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Provider abbreviation.",
            example = "SITES"
    )
    @JsonProperty("abbreviation")
    public String abbreviation;
    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Provider logo.",
            example = "https://dst15js82dk7j.cloudfront.net/231546/95187636-P5q11.png"
    )
    @JsonProperty("logo")
    public String logo;
}