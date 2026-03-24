package me.ropy.mysticbrews.config;

import me.ropy.mysticbrews.MysticBrews;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class BrewsConfig {

    private String bartenderName;

    public BrewsConfig(){
        loadConfig();
    }

    private void loadConfig() {
        MysticBrews plugin = MysticBrews.getInstance();
        File configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
            plugin.getLogger().info("Created default config.yml");
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        bartenderName = config.getString("npc.bartender-name");
    }

    public void reload(){
        MysticBrews.getInstance().getLogger().info("Reloading brews config...");
        loadConfig();
    }

    public String getBartenderName(){
        return this.bartenderName;
    }
}
