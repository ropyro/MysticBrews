package me.ropy.mysticbrews;

import me.ropy.mysticbrews.command.MysticBrewsCommand;
import me.ropy.mysticbrews.components.ComponentManager;
import me.ropy.mysticbrews.config.BrewsConfig;
import me.ropy.mysticbrews.item.BrewRegistry;
import me.ropy.mysticbrews.listener.*;
import me.ropy.mysticbrews.npc.NPCManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class MysticBrews extends JavaPlugin {

    private static MysticBrews INSTANCE;
    private static Economy ECON = null;

    private BrewsManager brewsManager;
    private ComponentManager componentManager;
    private NPCManager npcManager;
    private BrewsConfig pluginConfig;

    @Override
    public void onEnable() {
        INSTANCE = this;

        //register command
        getCommand("mysticbrews").setExecutor(new MysticBrewsCommand());

        //Setup/check for dependencies
        if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - Vault economy not found. Economy based rewards disabled.", getDescription().getName()));
        }
        if (getServer().getPluginManager().getPlugin("DecentHolograms") == null) {
            getLogger().severe(String.format("[%s] - DecentHolograms not found. Holographic leaderboards disabled.", getDescription().getName()));
        }
        if (getServer().getPluginManager().getPlugin("Citizens") == null) {
            getLogger().severe(String.format("[%s] - Citizens not found. NPCs disabled.", getDescription().getName()));
        }

        brewsManager = new BrewsManager();
        componentManager = new ComponentManager();
        npcManager = new NPCManager();
        pluginConfig = new BrewsConfig();

        BrewRegistry.registerBrews();

        //register listeners
        registerListeners(
                new NPCRightClickListener(),
                new PlayerQuitListener(),
                new PlayerItemConsumeListener(),
                new ChairListeners(),
                new JukeBoxListeners());
    }

    @Override
    public void onDisable() {
        brewsManager.close();
    }

    public BrewsManager getBrewsManager() {
        return brewsManager;
    }

    public ComponentManager getComponentManager() {
        return componentManager;
    }

    public NPCManager getNpcManager() {
        return npcManager;
    }

    public BrewsConfig getConfigLoader() {
        return this.pluginConfig;
    }

    //boilerplate vault economy setup
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        ECON = rsp.getProvider();
        return ECON != null;
    }

    public static Economy getEconomy() {
        return ECON;
    }

    //registers all listeners in the listener array argument
    private void registerListeners(Listener... listeners) {
        for (Listener listener : listeners)
            getServer().getPluginManager().registerEvents(listener, this);
    }

    public static MysticBrews getInstance() {
        return INSTANCE;
    }
}
