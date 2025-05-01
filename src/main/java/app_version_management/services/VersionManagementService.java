package app_version_management.services;

import app_version_management.entities.Version;

public interface VersionManagementService {
    Boolean uploadNewVersion(Version version);
    void createUpdatePatch(String appName, Float fromVersionId, Float toVersionId);
    void releaseVersion(String appName, Float versionId, String releaseStrategy);
    Boolean isAppVersionSupported(String appName, Float versionId, String deviceId);
    Boolean checkForInstall(String appName, String deviceId);
    Boolean checkForUpdates(String appName, String deviceId);
}
