package app_version_management.strategy;

import app_version_management.entities.Device;
import app_version_management.repositories.DeviceRepository;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class PercentageRollOutStrategy implements RolloutStrategy {

    private final DeviceRepository deviceRepository;

    public PercentageRollOutStrategy() {
        deviceRepository = DeviceRepository.getInstance();
    }

    @Override
    public void rollout(String appName, Float versionId) {
        List<Device> devices = deviceRepository.getDevices();
        List<Device> chosenDevices = devices.stream().limit((long) new Random().nextInt() * devices.size()).collect(Collectors.toList());
        chosenDevices.forEach(device -> {
            System.out.printf("Rolling out app: %s, version: %f for device: %d%n", appName, versionId, device.getId());
        });
    }
}
