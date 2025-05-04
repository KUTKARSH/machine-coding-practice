package app_version_management.services;

import app_version_management.entities.Device;
import app_version_management.entities.Version;
import app_version_management.repositories.AppStoreRepository;
import app_version_management.repositories.DeviceRepository;
import app_version_management.repositories.VersionRepository;
import app_version_management.strategy.BetaRollOutStrategy;
import app_version_management.strategy.PercentageRollOutStrategy;

import java.util.*;

public class VersionManagementServiceImpl implements VersionManagementService {

    private VersionManagementServiceImpl versionManagementService;
    private final AppStoreRepository appStoreRepository;
    private final DeviceRepository deviceRepository;
    private final VersionRepository versionRepository;
    private final RollOutServiceImpl rollOutService;

    private VersionManagementServiceImpl() {
        appStoreRepository = AppStoreRepository.getInstance();
        deviceRepository = DeviceRepository.getInstance();
        versionRepository = VersionRepository.getInstance();
        rollOutService = new RollOutServiceImpl();
    }

    public VersionManagementServiceImpl getInstance() {
        if (versionManagementService == null) {
            versionManagementService = new VersionManagementServiceImpl();
        }
        return versionManagementService;
    }

    @Override
    public Boolean uploadNewVersion(Version version) {
        if (version.getVersionId() == null) {
            return false;
        }
        if (version.getAppName() == null || version.getAppName().isEmpty()) {
            return false;
        }
        if (versionRepository.getVersionMap().containsKey(version.getAppName())) {
            versionRepository.getVersionMap().get(version.getAppName()).add(version);
        } else {
            versionRepository.getVersionMap().put(version.getAppName(), new ArrayList<>());
            versionRepository.getVersionMap().get(version.getAppName()).add(version);
        }
        return true;
    }

    @Override
    public void createUpdatePatch(String appName, Float fromVersionId, Float toVersionId) {
        Optional<Version> fromVersionOpt = versionRepository.getVersionMap().get(appName).stream()
                .filter(version -> Objects.equals(version.getVersionId(), fromVersionId))
                .findFirst();
        Optional<Version> toVersionOpt = versionRepository.getVersionMap().get(appName).stream()
                .filter(version -> Objects.equals(version.getVersionId(), toVersionId))
                .findFirst();
        Version fromVersion = null,toVersion = null;
        if (!fromVersionOpt.isPresent() || !toVersionOpt.isPresent()) {
            return;
        }
        fromVersion = fromVersionOpt.get();
        toVersion = toVersionOpt.get();
        byte[] fromData = fromVersion.getData();
        byte[] toData = toVersion.getData();
        byte[] patchData = new byte[toData.length - fromData.length];
        for (int i = 0; i < patchData.length; i++) {
            patchData[i] = (byte) (toData[i + fromData.length] - fromData[i]);
        }
        System.out.println("Calling updateApp(diffPack)");
    }

    @Override
    public void releaseVersion(String appName, Float versionId, String releaseStrategyName) {
//        RollOutStrategy rolloutStrategy = RollOutStrategy.from(releaseStrategyName);
//        if (rolloutStrategy.equals(RollOutStrategy.PERCENTAGE)) {
//            rollOutService.rollout(appName, versionId, new PercentageRollOutStrategy());
//        }
//        if (rolloutStrategy.equals(RollOutStrategy.BETA) {
//            rollOutService.rollout(appName, versionId, new BetaRollOutStrategy());
//        }
    }

    @Override
    public Boolean isAppVersionSupported(String appName, Float versionId, String deviceId) {
        if (Objects.isNull(versionId) || Objects.isNull(deviceId)) {
            return false;
        }
       List<Version> versions = versionRepository.getVersionMap().get(appName);
       Optional<Version> versionOpt = versions.stream().filter(v -> v.getVersionId().equals(versionId)).findFirst();
       Version version;
       if (versionOpt.isPresent()) {
           version = versionOpt.get();
       } else {
           return false;
       }
       Optional<Device> deviceOpt = deviceRepository.getDevices().stream().filter(d -> d.getId().equals(deviceId)).findFirst();
       Device device = null;
       if (deviceOpt.isPresent()) {
           device = deviceOpt.get();
       }
        return version.getSupportedOs().equals(device.getOs()) && version.getMinimumOsVersion() <= device.getOsVersion();
    }

    @Override
    public Boolean checkForInstall(String appName, String deviceId) {
        Optional<Device> deviceOpt = deviceRepository.getDevices().stream().filter(d -> d.getId().equals(deviceId)).findFirst();
        Device device = deviceOpt.orElse(null);
        List<Version> versions = versionRepository.getVersionMap().get(appName);
        Optional<Version> supportedVersion = versions.stream().filter(version -> version.getSupportedOs().equals(device.getOs()) && version.getMinimumOsVersion() <= device.getOsVersion()).findFirst();
        return supportedVersion.isPresent();
    }

    @Override
    public Boolean checkForUpdates(String appName, String deviceId) {
        Optional<Device> deviceOpt = deviceRepository.getDevices().stream().filter(d -> d.getId().equals(deviceId)).findFirst();
        Device device = deviceOpt.orElse(null);
        HashMap<String, Float> apps = device.getInstalledApps();
        List<Version> versions = versionRepository.getVersionMap().get(appName);
        versions.sort((v1, v2) -> (int) (v2.getVersionId() * 100 - v1.getVersionId() * 100));
        return device.getInstalledApps().get(appName) < versions.get(0).getVersionId();
    }
}
