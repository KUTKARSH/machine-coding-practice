package app_version_management.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Version {
    private String appName;
    private String supportedOs;
    private Float minimumOsVersion;
    private Float versionId;
    private byte[] data;
}
