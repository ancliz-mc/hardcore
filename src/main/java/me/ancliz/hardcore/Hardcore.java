package me.ancliz.hardcore;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import me.ancliz.hardcore.commands.completers.DefaultTabCompleter;
import me.ancliz.hardcore.listeners.PlayerDeathListener;
import me.ancliz.hardcore.listeners.PlayerPortalListener;
import me.ancliz.hardcore.util.LoggerWrapper;

@SuppressWarnings("deprecation")
public final class Hardcore extends JavaPlugin {
    private static final LoggerWrapper logger = new LoggerWrapper(LogManager.getLogger());
    private static Hardcore instance;

    @Override
    public void onEnable() {
        instance = this;
        setupCommands();
        registerListeners();
    }

    @Override
    public void onDisable() {

    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerPortalListener(), this);
        
    }

    private void setupCommands() {
        Map<String, Map<String, Object>> commands = getDescription().getCommands();
        commands.forEach(this::setExecutor);
        getCommand("hardcore").setUsage("Unknown command, type /hardcore help for available commands.");
    }

    @SuppressWarnings("unchecked")
    private void setTabCompleter(String command) throws Exception {
        Class<? extends TabCompleter> clazz = null;
        try {
            logger.trace("Setting {} completer", command);
            clazz = (Class<? extends TabCompleter>) Class.forName("me.ancliz.hardcore.commands.completers.TabCompleter" + command);
        } catch(ClassNotFoundException e) {
            logger.warn("No tab completion for " + command + ". Setting to default.");
            clazz = DefaultTabCompleter.class;
        } finally {

            getCommand(command).setTabCompleter(clazz.getDeclaredConstructor().newInstance());
        }
    }

    @SuppressWarnings("unchecked")
    private void setExecutor(String command, Map<String, Object> tree) {
        command = command.substring(0, 1).toUpperCase() + command.substring(1);
        try {
            logger.trace("Setting {} executor", command);
            Class<? extends CommandExecutor> clazz = (Class<? extends CommandExecutor>) Class.forName("me.ancliz.hardcore.commands.Command" + command);
            getCommand(command).setExecutor(clazz.getDeclaredConstructor().newInstance());
            setTabCompleter(command);

        } catch(ClassNotFoundException e) {
            logger.error("Command Executor class could not be found for " + command);
        } catch(Exception e) {
            logger.error("Error loading commands.", e);
        }
    }

    public static Hardcore getInstance() {
        return instance;
    }

}