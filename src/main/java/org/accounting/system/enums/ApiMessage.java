package org.accounting.system.enums;

public enum ApiMessage {


    PAGE_NUMBER("Page number must be >= 1."),
    SWAGGER_PAGE_NUMBER("Indicates the page number. Page number must be >= 1."),
    UNAUTHORIZED_CLIENT("The authenticated client is not permitted to perform the requested operation."),
    NO_PERMISSION("The authenticated client is not permitted to perform the requested operation.");

    public final String message;

    ApiMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
