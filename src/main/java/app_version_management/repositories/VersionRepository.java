package app_version_management.repositories;

import app_version_management.entities.Version;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class VersionRepository implements RepositoryInterface {

    private static VersionRepository instance = null;
    private HashMap<String, List<Version>> versionMap;

    private VersionRepository() {
        versionMap = new HashMap<>();
    }

    public static VersionRepository getInstance() {
        if (instance == null) {
            instance = new VersionRepository();
        }
        return instance;
    }
}
