package org.accounting.system.services.groupmanagement;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TenantGroupManagement implements GroupManagement{

    @SuppressWarnings("java:S1186")
    @Override
    public void createProjectGroup(String projectId) {
        // intentionally left blank
    }

    @SuppressWarnings("java:S1186")
    @Override
    public void createProviderGroup(String providerId) {
        // intentionally left blank
    }

    @SuppressWarnings("java:S1186")
    @Override
    public void createInstallationGroup(String projectId, String providerId, String installationId) {
        // intentionally left blank
    }

    @SuppressWarnings("java:S1186")
    @Override
    public void createAssociationGroup(String projectId, String providerId) {
        // intentionally left blank
    }

    @SuppressWarnings("java:S1186")
    @Override
    public void deleteProjectGroup(String projectId) {
        // intentionally left blank
    }

    @SuppressWarnings("java:S1186")
    @Override
    public void deleteProviderGroup(String providerId) {
        // intentionally left blank
    }

    @SuppressWarnings("java:S1186")
    @Override
    public void deleteInstallationGroup(String projectId, String providerId, String installationId) {
        // intentionally left blank
    }

    @SuppressWarnings("java:S1186")
    @Override
    public void deleteAssociationGroup(String projectId, String providerId) {
        // intentionally left blank
    }
}
