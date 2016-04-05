package de.steuerungc.mrtp;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Martin on 01.04.2016.
 */
public class Main extends JavaPlugin {

    private PluginLogger log = new PluginLogger(this);
    private Config c;
    private boolean status = false;
    protected static WorldChecker wc;
    protected static TaskHandler th;

    @Override
    public void onEnable() {
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {}

        try {
            c = new Config(this);
        } catch (Exception ex) {
            log.warning("The Config is not write or loadable! Disabling...");
            ex.printStackTrace();
            status = false;
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        ArrayList<String> errors = new ConfigValidator().validateConfig(c, this);
        if(errors == null) {
            log.info("The config was loaded successfully");
            status = true;
        } else {
            log.warning("Ouch... There is/are " + errors.size() + " Problem(s) in your config:");
            for (String s : errors) {
                log.warning(s);
            }
            log.warning("Please check these errors. Reload config with /rtp reload!");
            status = false;
        }

        if(status) {
            wc = new WorldChecker(c.getStringList("enabled_worlds"), c.getMessage("messages.prefix"));
            th = new TaskHandler(this, wc);
            getCommand("randomtp").setExecutor(new CommandHandler(this, th));
            getServer().getPluginManager().registerEvents(new ActionHandler(this, th), this);
        } else {
            getCommand("randomtp").setExecutor(new CommandHandler(this, null));
            getServer().getPluginManager().registerEvents(new ActionHandler(this, null), this);
        }
    }

    public Config sendConfig() {
        return c;
    }

    public void warn(String s) {
        log.warning(s);
    }

    public boolean isWorking() {
        return status;
    }

    protected void performReload(CommandSender cs, String savedpre) {
        try {
            c = new Config(this);
        } catch (Exception ex) {
            log.warning("The Config is not write or loadable! Disabling...");
            ex.printStackTrace();
            status = false;
            getServer().getPluginManager().disablePlugin(this);
            cs.sendMessage("§4A fatal error occurred while reloading. Please refer to the log! Plugin is disabling.");
            return;
        }

        ArrayList<String> errors = new ConfigValidator().validateConfig(c, this);
        if(errors == null) {
            log.info("The config was loaded successfully");
            status = true;
        } else {
            log.warning("Ouch... There is/are " + errors.size() + " Problem(s) in your config:");
            for (String s : errors) {
                log.warning(s);
            }
            log.warning("Please check these errors. Reload config with /rtp reload!");
            status = false;
        }

        if (status) {
            cs.sendMessage(savedpre + " §2Reloaded sucessfully!");
            wc = new WorldChecker(c.getStringList("enabled_worlds"), c.getMessage("messages.prefix"));
            th = new TaskHandler(this, wc);
            getCommand("randomtp").setExecutor(new CommandHandler(this, th));
            getServer().getPluginManager().registerEvents(new ActionHandler(this, th), this);
        } else {
            cs.sendMessage(savedpre + " §4There is an error in your config. Please check the log!");
            getCommand("randomtp").setExecutor(new CommandHandler(this, null));
            getServer().getPluginManager().registerEvents(new ActionHandler(this, null), this);
        }
    }
}
