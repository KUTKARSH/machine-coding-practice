package app_version_management.repositories;

import app_version_management.entities.App;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
@AllArgsConstructor
public class AppStoreRepository implements RepositoryInterface {

    private static AppStoreRepository instance = null;

    private HashMap<String, App> appMap;

    private AppStoreRepository() {
        appMap = new HashMap<>();
    }

    public static AppStoreRepository getInstance() {
        if (instance == null) {
            instance = new AppStoreRepository();
        }
        return instance;
    }
}
