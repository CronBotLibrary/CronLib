package jp.cron.cronlib.config;

import org.yaml.snakeyaml.Yaml;

import java.io.*;

public abstract class ConfigReader<T> {

    private File configFile;
    private Yaml yaml;

    private Class<T> clazz;
    private T config;

    public ConfigReader(Class<T> clazz, File configFile) {
        this.configFile = configFile;
        this.yaml = new Yaml();
        this.clazz = clazz;
    }

    public void start() {
        try (InputStream inputStream  = new FileInputStream(this.configFile)) {
            this.config = this.yaml.loadAs(inputStream, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config : "+configFile.getPath(), e);
        }
    }

    public T getConfig() {
        if (this.config == null) {
            throw new IllegalStateException("Not loaded or throwed error while loading");
        }

        return this.config;
    }

}
