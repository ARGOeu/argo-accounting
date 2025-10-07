package org.accounting.system.entities.projections.normal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

@Schema(name="ProviderProjectionResponse", description="An object represents a Provider containing the Installations assigned to it.")
@JsonPropertyOrder({"id", "name", "external_id", "website", "abbreviation", "logo", "installations" })
public class ProviderProjection {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Provider ID.",
            example = "607f1f77bcf86cd799439011"
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

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Provider external ID.",
            example = "sites"
    )
    @JsonProperty("external_id")
    @BsonProperty("external_id")
    public String externalId;

    @Schema(
            type = SchemaType.ARRAY,
            implementation = ProjectionInstallation.class,
            description = "The Installation associated with a Provider."
    )
    @JsonProperty("installations")
    public List<ProjectionInstallation> installations;

    public ProviderProjection(){

        this.installations = new ArrayList<>();
    }
}
