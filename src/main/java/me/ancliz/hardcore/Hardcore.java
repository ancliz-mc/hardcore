package me.ancliz.hardcore;

import org.bukkit.Bukkit;
import me.ancliz.hardcore.listeners.PlayerDeathListener;
import me.ancliz.hardcore.listeners.PlayerPortalListener;
import me.ancliz.hardcore.listeners.PortalCreateListener;
import me.ancliz.minecraft.AnkyPlugin;

public final class Hardcore extends AnkyPlugin {

    @Override
    public void onEnable() {
        super.onEnable();
        getConfig().options().copyDefaults(true);
        saveConfig();
        registerListeners();
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerPortalListener(), this);
        Bukkit.getPluginManager().registerEvents(new PortalCreateListener(), this);
    }

    @Override
    public void reload() {
        commandManager.reload();
        reloadConfig();  
    }

    @Override
    public void onDisable() {}

}