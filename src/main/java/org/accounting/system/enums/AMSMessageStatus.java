package org.accounting.system.enums;

import lombok.Getter;

public enum AMSMessageStatus {

    CREATE_PROVIDER("approved provider"),
    CREATE_INSTALLATION("approved resource");

    @Getter
    private final String value;

    AMSMessageStatus(String value) {
        this.value = value;
    }
}
