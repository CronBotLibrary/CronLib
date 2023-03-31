package jp.cron.cronlib;

import jp.cron.cronlib.api.JavaActivityHandler;
import jp.cron.cronlib.api.JavaFeature;
import jp.cron.cronlib.bot.Bot;
import jp.cron.cronlib.config.main.MainConfigReader;
import jp.cron.cronlib.feature.config.FeatureConfig;
import jp.cron.cronlib.feature.loader.FeatureLoader;
import jp.cron.cronlib.feature.manager.FeatureManager;
import net.dv8tion.jda.api.OnlineStatus;

import java.io.File;
import java.util.List;
import java.util.Map;

public class Application {
    private static Application APPLICATION;

    public boolean debug = false;
    public Class<?> sourceClass = null;

    public File featuresFolder;

    public MainConfigReader mainConfig;

    public FeatureManager featureManager;

    private FeatureLoader featureLoader;

    public Bot bot;

    public Application() {
        if (APPLICATION!=null)
            throw new IllegalStateException("Already initalized Application.");
        APPLICATION = this;
        featuresFolder = new File("feats");
        mainConfig = new MainConfigReader();
        featureLoader = new FeatureLoader(this);
        featureManager = null;
        this.bot = new Bot(this);
    }

    public void start() {
        mainConfig.copyDefault();
        mainConfig.start();

        if (!featureLoader.checkCanRead())
            throw new IllegalStateException("Failed to load features.");

        List<JavaFeature> features = featureLoader.loadFeatures();
        featureManager = new FeatureManager(features);

        featureManager.start();

        bot.start(mainConfig.getConfig());
    }

    public static Application getApplication() {
        return APPLICATION;
    }

}
