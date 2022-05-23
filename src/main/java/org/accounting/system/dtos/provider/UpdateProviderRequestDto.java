package org.accounting.system.dtos.provider;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="UpdateProviderRequest", description="An object represents a request for updating a Provider.")
public class UpdateProviderRequestDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Provider ID.",
            example = "sites"
    )
    @JsonProperty("id")
    public String id;
    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Provider name.",
            example = "Swedish Infrastructure for Ecosystem Science"
    )
    @JsonProperty("name")
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