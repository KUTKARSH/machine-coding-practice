package app_version_management.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
@Builder
public class Device {
    private Long id;
    private String os;
    private Float osVersion;
    private HashMap<String, Float> installedApps;
}
