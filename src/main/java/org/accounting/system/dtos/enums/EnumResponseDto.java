package org.accounting.system.dtos.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public class EnumResponseDto {


    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Label of operator",
            example = "eq"
    )
    @JsonProperty("label")
    public String label;


    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Display Value of operator",
            example = "=="
    )
    @JsonProperty("display_value")
    public String displayValue;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }
}
