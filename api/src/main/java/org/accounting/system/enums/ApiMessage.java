package org.accounting.system.enums;

public enum ApiMessage {


    PAGE_NUMBER("Page number must be >= 1."),
    SWAGGER_PAGE_NUMBER("Indicates the page number. Page number must be >= 1."),
    UNAUTHORIZED_CLIENT("Access denied."),
    NO_PERMISSION("Access denied.");

    public final String message;

    ApiMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
