package app_version_management.strategy;

import app_version_management.entities.Device;
import app_version_management.repositories.DeviceRepository;

import java.util.List;

public class BetaRollOutStrategy implements RolloutStrategy {

    private final DeviceRepository deviceRepository;

    public BetaRollOutStrategy() {
        deviceRepository = DeviceRepository.getInstance();
    }

    @Override
    public void rollout(String appName, Float versionId) {
        List<Device> devices = deviceRepository.getDevices();
        devices.forEach(device -> {
            if (device.getId() % 2 == 0) {
                System.out.printf("Rolling out app: %s, version: %f for device: %d%n", appName, versionId, device.getId());
            }
        });
    }
}
