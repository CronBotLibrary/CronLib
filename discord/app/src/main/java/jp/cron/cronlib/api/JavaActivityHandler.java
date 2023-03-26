package jp.cron.cronlib.api;

import jp.cron.cronlib.Application;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.List;

public abstract class JavaActivityHandler {
    boolean isEnabled = false;

    public JavaActivityHandler(Application app) {
    }

    public abstract Activity getActivity();
    public abstract OnlineStatus getOnlineStatus();



}
