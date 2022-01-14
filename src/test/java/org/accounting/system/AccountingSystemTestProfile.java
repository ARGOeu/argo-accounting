package org.accounting.system;

import io.quarkus.test.junit.QuarkusTestProfile;

public class AccountingSystemTestProfile implements QuarkusTestProfile {

    @Override
    public boolean disableApplicationLifecycleObservers() {
        return true;
    }
}
