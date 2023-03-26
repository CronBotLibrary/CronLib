package jp.cron.cronlib.feature.example;

import jp.cron.cronlib.Application;
import jp.cron.cronlib.api.Command;
import jp.cron.cronlib.api.JavaFeature;
import jp.cron.cronlib.api.Listener;

import java.util.List;

public class ExampleFeature extends JavaFeature {
    @Override
    public void onEnable() {
        System.out.println("ENABLE!");
    }

    @Override
    public void onDisable() {
        System.out.println("DISABLE!");
    }

}