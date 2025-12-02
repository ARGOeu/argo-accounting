package org.accounting.system.services.groupmanagement;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DatabaseGroupManagementFactory implements GroupManagementFactoryI{

    @Inject
    DatabaseGroupManagement databaseGroupManagement;

    @Override
    public GroupManagement choose() {
        return databaseGroupManagement;
    }
}
