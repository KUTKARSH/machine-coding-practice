package app_version_management.repositories;

import app_version_management.entities.Device;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DeviceRepository implements RepositoryInterface {

    private static DeviceRepository instance = null;
    private List<Device> devices;

    private DeviceRepository() {
        devices = new ArrayList<>();
    }

    public static DeviceRepository getInstance() {
        if (instance == null) {
            instance = new DeviceRepository();
        }
        return instance;
    }
}
