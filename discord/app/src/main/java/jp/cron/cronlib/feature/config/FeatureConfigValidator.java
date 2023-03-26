package jp.cron.cronlib.feature.config;

public class FeatureConfigValidator {

    public void validate(FeatureConfig config) throws IllegalStateException {
        if (config.name==null || config.name.isEmpty()) {
            throw new IllegalStateException("Feature's name must be not null or empty.");
        }
        if (config.version==null || config.version.isEmpty()) {
            throw new IllegalStateException("Feature's version must be not null or empty.");
        }
        if (config.mainClass==null || config.mainClass.isEmpty()) {
            throw new IllegalStateException("Feature's mainClass must be not null or empty.");
        }
    }
}
