package org.accounting.system.services.groupmanagement;

public interface GroupManagement{

    void createProjectGroup(String projectId);

    void createProviderGroup(String providerId);

    void createInstallationGroup(String projectId, String providerId, String installationId);

    void createAssociationGroup(String projectId, String providerId);

    void deleteProjectGroup(String projectId);

    void deleteProviderGroup(String providerId);

    void deleteInstallationGroup(String projectId, String providerId, String installationId);

    void deleteAssociationGroup(String projectId, String providerId);

}
