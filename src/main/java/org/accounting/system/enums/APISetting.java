package org.accounting.system.enums;

import java.util.List;

public enum APISetting {

    ENTITLEMENTS_MANAGEMENT("database", List.of("oidc", "database"));

    private final String defaultValue;

    private final List<String> allowedValues;

    APISetting(String defaultValue, List<String> allowedValues) {
        this.defaultValue = defaultValue;
        this.allowedValues = allowedValues;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public List<String> getAllowedValues() {
        return allowedValues;
    }

    public boolean isValidValue(String value) {
        return allowedValues == null || allowedValues.contains(value);
    }
}