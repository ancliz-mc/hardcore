package me.ancliz.hardcore.listeners;

import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import me.ancliz.hardcore.Hardcore;
import me.ancliz.hardcore.WorldMetaData;
import me.ancliz.hardcore.actions.WorldAction;
import me.ancliz.hardcore.util.LoggerWrapper;
import me.ancliz.hardcore.util.Metadata;

public class PlayerDeathListener implements Listener {
    private static final LoggerWrapper logger = new LoggerWrapper(LogManager.getLogger());
    private WorldAction worldAction;
    private YamlConfiguration statistics;
    private Hardcore plugin;

    public PlayerDeathListener() {
        plugin = Hardcore.getInstance();
        statistics = plugin.getYaml("statistics.yml");
        worldAction = new WorldAction();
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {

        Player player = event.getPlayer();
        World currentWorld = player.getWorld();
        String groupName = Metadata.getWorldGroup(currentWorld);
        String baseName = Metadata.getWorldBaseName(currentWorld);

        statistics.set("attempts", statistics.getInt("attempts") + 1);

        String newWorldGroup = baseName + statistics.getString("attempts");
        FileConfiguration config = plugin.getConfig();

        worldAction.createWorldGroup(newWorldGroup,
                Metadata.mapBuilder((plugin, value) -> new WorldMetaData(plugin, value))
                        .put("base-name", baseName)
                        .put("group", newWorldGroup)
                        .put("iteration", statistics.getInt("attempts"))
                        .build());

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    worldAction.teleportToWorld(Bukkit.getWorld(newWorldGroup), Bukkit.getServer().getOnlinePlayers());
                    worldAction.revokeAdvancements();
                }, config.getLong("world-teleport-delay"));

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if(groupName.equals("world")) {
                logger.warn("Attempting to unload default worlds, aborting.");
                return;
            }
            worldAction.unloadWorldGroup(groupName);
            worldAction.deleteWorldGroup(groupName);
        }, config.getLong("world-delete-delay"));

        plugin.saveYaml(statistics, "statistics.yml");
    }

}