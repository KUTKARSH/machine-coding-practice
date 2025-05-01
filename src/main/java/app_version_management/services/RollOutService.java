package app_version_management.services;

import app_version_management.strategy.RolloutStrategy;

public interface RollOutService {
    void rollout(String appName, Float versionId, RolloutStrategy rolloutStrategy);

}
