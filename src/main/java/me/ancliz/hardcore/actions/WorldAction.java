package me.ancliz.hardcore.actions;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import me.ancliz.hardcore.WorldMetaData;
import me.ancliz.hardcore.util.LoggerWrapper;

public class WorldAction {
    LoggerWrapper logger = new LoggerWrapper(LogManager.getLogger());

    public void groupMetadata(World world, Map<String, WorldMetaData> data) {
        data.forEach(world::setMetadata);
    }

    public World createWorld(String fullyQualifiedName, Environment environment) {
        return createWorld(fullyQualifiedName, environment, new HashMap<>());
    }

    public World createWorld(String fullyQualifiedName, Environment environment, Map<String, WorldMetaData> data) {
        if(!Bukkit.isTickingWorlds()) {
            WorldCreator creator = new WorldCreator(fullyQualifiedName).environment(environment);
            groupMetadata(Bukkit.createWorld(creator), data);
            logger.info("New world '{}' created.", fullyQualifiedName);
        }
        return null;
    }

    public String createWorldGroup(String group, Map<String, WorldMetaData> data) {
        createWorld(group + "_nether", Environment.NETHER, data);
        createWorld(group, Environment.NORMAL, data);
        createWorld(group + "_the_end", Environment.THE_END);
        return group;
    }

    public boolean deleteWorldGroup(String name) {
        return deleteWorld(name)
            && deleteWorld(name + "_nether") 
            && deleteWorld(name + "_the_end");
    }

    public String[] unloadWorldGroup(String name) {
        String[] paths = new String[3];
        paths[0] = unloadWorld(name);
        paths[1] = unloadWorld(name + "_nether");
        paths[2] = unloadWorld(name + "_the_end");
        return paths;
    }

    public String unloadWorld(String world) throws NullPointerException {
        String path = Bukkit.getWorld(world).getWorldFolder().getAbsolutePath();
        logger.info("Unloading {}", world);

        if(!Bukkit.isTickingWorlds()) {
            if(Bukkit.unloadWorld(world, false)) {
                logger.info("Unloaded {}", world);
            }
        }
        return path;
    }

    public boolean deleteWorld(String worldName) {
        World world = Bukkit.getWorld(worldName);
        logger.warn("Deleting {}", worldName);
        
        if(world == null) {
            File file = new File(Bukkit.getServer().getWorldContainer() + "/" + worldName);
            if(file.exists()) {
                delete(file);
            }
            return true;
        }
        return false;
    }

    private void delete(File file) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                delete(f);
            }
        }
        file.delete();
    }

    public void teleportToWorld(World world, Collection<? extends Player> players) {
        for(Player player : players) {
            player.teleport(world.getSpawnLocation());
        }
        logger.info("Teleported players to {}", world.getName());
    }

    public boolean teleportToWorld(String worldName, Player player) {
        World world = Bukkit.getWorld(worldName);
        if(world == null) {
            return false;
        }
        player.teleport(world.getSpawnLocation());
        return true;
    }

}