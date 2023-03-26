package jp.cron.cronlib.bot;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import jp.cron.cronlib.Application;
import jp.cron.cronlib.api.Command;
import jp.cron.cronlib.api.JavaActivityHandler;
import jp.cron.cronlib.api.JavaFeature;
import jp.cron.cronlib.api.Listener;
import jp.cron.cronlib.bot.impl.CommandClientBuilder;
import jp.cron.cronlib.config.main.MainConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.*;

public class Bot extends ListenerAdapter {

    private CommandClient client;
    private JDA jda;
    private EventWaiter eventWaiter;

    private Application app;

    private Map<JavaFeature, List<Listener>> listeners;
    private Map<Command, JavaFeature> commands;
    private List<GatewayIntent> intents;
    private JavaActivityHandler activityHandler;
    private JavaFeature activityHandlerJavaFeature;


    public Bot(Application app) {
        this.app = app;
        jda = null;
        eventWaiter = null;

        this.listeners = new HashMap<>();
        this.commands = new HashMap<>();
        this.intents = new ArrayList<>();
    }

    public void start(MainConfig mainConfig) {
        eventWaiter = new EventWaiter();

        client = commandClientBuilder(
                "",
                mainConfig.ownerId,
                mainConfig.coOwnerIds
        ).build();

        this.jda = jdaBuilder(
                mainConfig.token,
                0,
                1,
                client
        ).build();

    }

    public JDA getJda() {
        return this.jda;
    }

    private CommandClientBuilder commandClientBuilder(String prefix, String ownerId, String[] coOwnerIds) {
        CommandClientBuilder builder = new CommandClientBuilder()
                .setPrefix(prefix)
                .setOwnerId(ownerId)
                .setCoOwnerIds(coOwnerIds)
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.playing("準備中... / Loading..."))
                .useHelpBuilder(false);

        for (Map.Entry<Command, JavaFeature> entry : commands.entrySet()) {
            builder.addSlashCommand(entry.getKey(), entry.getValue());
        }
        return builder;
    }

    public JDABuilder jdaBuilder(String token, int shardId, int shardTotal, CommandClient commandClient) {
        JDABuilder jdaBuilder = JDABuilder.create(token, intents)
                .setBulkDeleteSplittingEnabled(true)
                .setAutoReconnect(true)
                .setEnableShutdownHook(true)
                .addEventListeners(eventWaiter, commandClient, this)
                .setActivity(Activity.playing("準備中... / Loading..."))
                .useSharding(
                        shardId,
                        shardTotal
                );

        listeners.values().forEach(
                l -> l.forEach(jdaBuilder::addEventListeners)
        );
        return jdaBuilder;
    }

    public void registerListener(Listener listener, JavaFeature javaFeature) {
        if (!listeners.containsKey(javaFeature))
            listeners.put(javaFeature, new ArrayList<>());
        listeners.get(javaFeature).add(listener);
        if (jda!=null)
            jda.addEventListener(listener);
    }

    public void unloadFeatureListener(JavaFeature feature) {
        if (jda!=null && listeners.containsKey(feature)) {
            for (Listener listener : listeners.get(feature)) {
                jda.removeEventListener(listener);
            }
        }
        listeners.put(feature, new ArrayList<>());
    }

    public void registerCommand(Command command, JavaFeature javaFeature) {
        if (client!=null)
            client.addSlashCommand(command);
        else
            commands.put(command, javaFeature);
    }

    public void addIntents(GatewayIntent ...addIntents) {
        if (jda!=null)
            throw new IllegalStateException("Intents must be added before starting the bot!");
        else
            for (GatewayIntent addIntent : addIntents) {
                if (!intents.contains(addIntent))
                    intents.add(addIntent);
            }
    }

    public void setActivityHandler(JavaActivityHandler activityHandler, JavaFeature javaFeature) {
        if (!javaFeature.getInfo().handleActivity)
            throw new IllegalStateException("The JavaFeature is not registered as ActivityHandler");
        this.activityHandler = activityHandler;
        this.activityHandlerJavaFeature = javaFeature;
    }

    public void updateActivity() {
        if (jda==null)
            return;
        if (activityHandler!=null) {
            jda.getPresence().setActivity(activityHandler.getActivity());
            jda.getPresence().setStatus(activityHandler.getOnlineStatus());
        } else {
            jda.getPresence().setActivity(null);
            jda.getPresence().setStatus(OnlineStatus.ONLINE);
        }
    }

    @Override
    public void onReady(ReadyEvent event) {
        this.updateActivity();
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        this.updateActivity();
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        this.updateActivity();
    }
}
