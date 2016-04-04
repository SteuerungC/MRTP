package de.steuerungc.mrtp;

import java.io.File;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
/**
 * Created by Martin on 01.04.2016.
 */

public class Config {
    private static YamlConfiguration config;
    Main m;

    public Config(Main m) {
        this.m = m;
        File data = new File(m.getDataFolder().toString() + "/config.yml");
        if (!data.exists()) {
            m.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(data);
    }

    public static ConfigurationSection get(String section) {
        return config.getConfigurationSection(section);
    }

    public static YamlConfiguration getConfig() {
        return config;
    }

    public boolean getBoolean(String path) {
        String[] full = path.split("\\.");
        if (full.length == 1) {
            return Boolean.parseBoolean(config.getString(full[0]));
        } else {
            ConfigurationSection cs = this.get(full[0]);
            return Boolean.parseBoolean(cs.getString(full[1]));
        }
    }

    public int getInteger(String path) {
        String[] full = path.split("\\.");
        if (full.length == 1) {
            return Integer.parseInt(config.getString(full[0]));
        } else {
            ConfigurationSection cs = this.get(full[0]);
            return Integer.parseInt(cs.getString(full[1]));
        }
    }

    public String getString(String path) {
        String[] full = path.split("\\.");
        if (full.length == 1) {
            return config.getString(full[0]);
        } else {
            ConfigurationSection cs = this.get(full[0]);
            return cs.getString(full[1]);
        }
    }

    public String getMessage(String path) {
        String[] full = path.split("\\.");
        if (full.length == 1) {
            return ChatColor.translateAlternateColorCodes('&', config.getString(full[0]));
        } else {
            ConfigurationSection cs = this.get(full[0]);
            return ChatColor.translateAlternateColorCodes('&', cs.getString(full[1]));
        }
    }

    public double getDouble(String path) {
        String[] full = path.split("\\.");
        if (full.length == 1) {
            return Double.parseDouble(config.getString(full[0]));
        } else {
            ConfigurationSection cs = this.get(full[0]);
            return Double.parseDouble(cs.getString(full[1]));
        }
    }

    public List<String> getStringList(String path) {
        String[] full = path.split("\\.");
        if (full.length == 1) {
            return config.getStringList(full[0]);
        } else {
            ConfigurationSection cs = this.get(full[0]);
            return cs.getStringList(full[1]);
        }
    }
}