package app_version_management.strategy;

public interface RolloutStrategy {
    void rollout(String appName, Float versionId);
}
