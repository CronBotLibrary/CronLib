package jp.cron.cronlib.config.main;

import jp.cron.cronlib.config.ConfigReader;

import java.io.File;

public class MainConfigReader extends ConfigReader<MainConfig> {
    public MainConfigReader() {
        super(MainConfig.class, new File("config.yml"));
    }
}
