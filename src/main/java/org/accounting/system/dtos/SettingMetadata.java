package org.accounting.system.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Schema(name = "SettingMetadata", description = "Represents metadata information for a configurable application setting.")
public class SettingMetadata {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The unique key of the setting, matching the enum value.",
            example = "ENTITLEMENTS_MANAGEMENT"
    )
    public String key;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The current value stored in the database for this setting.",
            example = "database"
    )
    @JsonProperty("current_value")
    public String currentValue;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The default value used when no custom value exists in the database.",
            example = "database"
    )
    @JsonProperty("default_value")
    public String defaultValue;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "List of all valid values accepted by this setting.",
            example = "[\"database\", \"oidc\"]"
    )
    @JsonProperty("allowed_values")
    public List<String> allowedValues;

    public SettingMetadata(String key, String currentValue, String defaultValue, List<String> allowedValues) {
        this.key = key;
        this.currentValue = currentValue;
        this.defaultValue = defaultValue;
        this.allowedValues = allowedValues;
    }

    public SettingMetadata() {}
}
