package app_version_management.services;

import app_version_management.strategy.RolloutStrategy;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RollOutServiceImpl implements RollOutService {

    @Override
    public void rollout(String appName, Float versionId, RolloutStrategy rolloutStrategy) {
        rolloutStrategy.rollout(appName, versionId);
    }
}
