package me.ancliz.hardcore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import com.google.common.base.Charsets;
import me.ancliz.hardcore.commands.completers.DefaultTabCompleter;
import me.ancliz.hardcore.listeners.PlayerDeathListener;
import me.ancliz.hardcore.listeners.PlayerPortalListener;
import me.ancliz.hardcore.listeners.PortalCreateListener;
import me.ancliz.hardcore.util.LoggerWrapper;

@SuppressWarnings("deprecation")
public final class Hardcore extends JavaPlugin {
    private static final LoggerWrapper logger = new LoggerWrapper(LogManager.getLogger());
    private static Hardcore instance;

    public void reload() {
        reloadConfig();  
    }

    @Override
    public InputStream getResource(String file) {
        try {
            return new FileInputStream(new File(getDataFolder(), file));
        } catch(FileNotFoundException e) {}
        logger.info("Getting embedded resource '{}'", file);
        return super.getResource(file);
    }

    public InputStream getEmbeddedResource(String file) {
        return super.getResource(file);
    }

    public YamlConfiguration getYaml(String file) {
        Reader reader = new InputStreamReader(getResource(file), Charsets.UTF_8);
        return YamlConfiguration.loadConfiguration(reader);
    }

    public void saveYaml(YamlConfiguration yaml, String file) {
        try {
            yaml.save(new File(getDataFolder(), file));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onEnable() {
        instance = this;
        setupCommands();
        registerListeners();
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {

    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerPortalListener(), this);
        Bukkit.getPluginManager().registerEvents(new PortalCreateListener(), this);
        
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