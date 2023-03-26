package jp.cron.cronlib.api;

import jp.cron.cronlib.Application;
import com.jagrosh.jdautilities.command.SlashCommand;
import jp.cron.cronlib.feature.config.FeatureConfig;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.File;
import java.util.List;

public abstract class JavaFeature {
    private FeatureConfig info;
    private Application app;
    boolean isEnabled = false;

    public JavaFeature() {
    }

    protected abstract void onEnable();
    protected abstract void onDisable();

    public void init(Application app, FeatureConfig config) {
        this.app = app;
        this.info = config;
    }

    public void enable() {
        onEnable();
        this.isEnabled = true;
    }

    public void disable() {
        this.isEnabled = false;
        onDisable();
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    protected Application getApp() {
        return this.app;
    }

    public FeatureConfig getInfo() {
        return this.info;
    }

    protected File getFeatureFolder() {
        File file = new File("feats/"+this.info.id);
        if (!file.canRead()||!file.canWrite()||!file.isDirectory())
            throw new IllegalStateException("The feature folder is not a folder!");
        return file;
    }
}