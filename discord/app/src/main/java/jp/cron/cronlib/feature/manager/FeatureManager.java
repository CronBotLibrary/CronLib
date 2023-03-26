package jp.cron.cronlib.feature.manager;

import jp.cron.cronlib.Application;
import jp.cron.cronlib.api.Command;
import jp.cron.cronlib.api.JavaActivityHandler;
import jp.cron.cronlib.api.JavaFeature;
import jp.cron.cronlib.api.Listener;
import jp.cron.cronlib.feature.config.FeatureConfig;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FeatureManager {

    private List<JavaFeature> features;

    public FeatureManager(List<JavaFeature> features) {
        this.features = features;
    }

    public void start() {
        features.forEach(this::load);
    }

    public void load(JavaFeature feat) {
        try {
            feat.enable();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void unload(JavaFeature feat) {
        try {
            feat.disable();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Application.getApplication().bot.unloadFeatureListener(feat);
    }

    public Collection<JavaFeature> getFeatures() {
        return features;
    }

    public FeatureConfig getFeatureInfo(JavaFeature feature) {
        return features.stream()
                .filter(e -> e.equals(feature))
                .findFirst().orElseThrow(()->new IllegalStateException("Unknown feature.")).getInfo();
    }

    public JavaFeature getFeature(String id) {
        return features.stream()
                .filter(e -> e.getInfo().id.equalsIgnoreCase(id))
                .findFirst().orElse(null);
    }





}
