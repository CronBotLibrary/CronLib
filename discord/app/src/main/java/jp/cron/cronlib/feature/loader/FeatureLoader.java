package jp.cron.cronlib.feature.loader;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jp.cron.cronlib.Application;
import jp.cron.cronlib.Main;
import jp.cron.cronlib.api.JavaActivityHandler;
import jp.cron.cronlib.api.JavaFeature;
import jp.cron.cronlib.feature.config.FeatureConfig;
import jp.cron.cronlib.feature.config.FeatureConfigValidator;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FeatureLoader {
    private final Application app;
    private final Gson gson;
    private final FeatureConfigValidator configValidator;

    public FeatureLoader(Application app) {
        this.app = app;
        this.gson = new Gson();
        this.configValidator = new FeatureConfigValidator();
    }

    public List<JavaFeature> loadFeatures() {
        List<JavaFeature> features = new ArrayList<>();

        for (File jar : listJars()) {
            try (ZipFile zipJar = new ZipFile(jar)) {
                ZipEntry entry = zipJar.getEntry("feature.json");
                if (entry==null) {
                    // TODO: log warning
                    // ignore a jar when it doesn't contain feature.json
                    continue;
                }

                InputStream stream = zipJar.getInputStream(entry);
                Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
                Type collectionType = new TypeToken<Collection<FeatureConfig>>(){}.getType();
                List<FeatureConfig> featureConfigs = gson.fromJson(reader, collectionType);

                for (FeatureConfig featureConfig : featureConfigs) {
                    configValidator.validate(featureConfig);
                    boolean alreadyLoaded = features.stream()
                            .anyMatch(ft -> ft.getInfo().id.equalsIgnoreCase(featureConfig.id));
                    if (alreadyLoaded) {
                        featureConfigs.remove(featureConfig);
                        // TODO: handle
                        continue;
                    }
                }

                if (featureConfigs.stream().filter(fc -> fc.handleActivity).count() > 1)
                    throw new IllegalStateException("Multiple ActivityHandler in single jar!");

                try (URLClassLoader child = new URLClassLoader(
                        new URL[] {jar.toURI().toURL()}, this.getClass().getClassLoader()
                )) {
                    features.addAll(
                            loadFromClassLoader(featureConfigs, child)
                    );
                }
            } catch (Exception e) {
                // TODO: execption
                e.printStackTrace();
            }
        }

        if (this.app.debug) {
            InputStream stream = this.app.sourceClass.getClassLoader().getResourceAsStream("feature-dev.json");
            if (stream!=null) {
                Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);

                Type collectionType = new TypeToken<Collection<FeatureConfig>>(){}.getType();
                List<FeatureConfig> featureConfigs = gson.fromJson(reader, collectionType);

                for (FeatureConfig featureConfig : featureConfigs) {
                    configValidator.validate(featureConfig);
                    boolean alreadyLoaded = features.stream()
                            .anyMatch(ft -> ft.getInfo().id.equalsIgnoreCase(featureConfig.id));
                    if (alreadyLoaded) {
                        featureConfigs.remove(featureConfig);
                        // TODO: handle
                        continue;
                    }
                }

                if (featureConfigs.stream().filter(fc -> fc.handleActivity).count() > 1)
                    throw new IllegalStateException("Multiple ActivityHandler in single jar!");

                features.addAll(
                        loadFromClassLoader(featureConfigs, this.app.sourceClass.getClassLoader())
                );
            }

        }

        return features;
    }

    public List<JavaFeature> loadFromClassLoader(List<FeatureConfig> featureConfigs, ClassLoader classLoader) {
        List<JavaFeature> features = new ArrayList<>();

        for (FeatureConfig featureConfig : featureConfigs) {
            try {
                Class<? extends JavaFeature> classToLoad = Class.forName(featureConfig.mainClass, true, classLoader).asSubclass(JavaFeature.class);
                JavaFeature feat = classToLoad.getDeclaredConstructor().newInstance();
                feat.init(app, featureConfig);
                features.add(feat);
            } catch (ClassNotFoundException | InvocationTargetException | InstantiationException |
                     IllegalAccessException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        return features;
    }

    private List<File> listJars() {
        return List.of(this.app.featuresFolder.listFiles(new JarFileFilter()));
    }

    public boolean checkCanRead() {
        return this.app.featuresFolder.canRead() && this.app.featuresFolder.isDirectory();
    }
}
