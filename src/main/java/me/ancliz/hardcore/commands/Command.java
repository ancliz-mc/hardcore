package me.ancliz.hardcore.commands;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.ancliz.hardcore.Hardcore;
import me.ancliz.hardcore.util.LoggerWrapper;

public enum Command {
    VERSION,
    RELOAD,
    HELP,
    WORLD,
    LIST,
    NEW,
    GOTO,
    UNLOAD,
    DELETE;
    
    private List<String> aliases = new ArrayList<>();
    private String description = "";
    private String usage = "";
    private final LoggerWrapper logger = new LoggerWrapper(LogManager.getLogger("Command." + toString()));

    Command() {
        FileConfiguration yaml = loadCommands();
        ConfigurationSection command = yaml.getConfigurationSection(this.toString().toLowerCase());
        if(command != null) {
            aliases = command.getStringList("aliases");
            aliases.add(toString());
            description = command.getString("description", "");
            usage = command.getString("usage", "");
        } else {
            logger.error("Command enum mismatch: {} not found in commands.yml", toString());
        }
    }

    private FileConfiguration loadCommands() {
        Hardcore plugin = Hardcore.getInstance();
        InputStream commands = plugin.getEmbeddedResource("commands.yml");

        if(commands == null) {
            throw new RuntimeException("Commands file not found.");   
        }

        return YamlConfiguration.loadConfiguration(new InputStreamReader(commands));
    }

    public List<String> aliases() {
        return aliases;
    }

    public String description() {
        return description;
    }

    public String usage() {
        return usage;
    }
    
}