package me.ancliz.hardcore.listeners;

import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import me.ancliz.hardcore.Hardcore;
import me.ancliz.hardcore.Statistics;
import me.ancliz.hardcore.WorldMetaData;
import me.ancliz.hardcore.actions.WorldAction;
import me.ancliz.hardcore.util.LoggerWrapper;
import me.ancliz.hardcore.util.Metadata;

public class PlayerDeathListener implements Listener {
    private static final LoggerWrapper logger = new LoggerWrapper(LogManager.getLogger());
    private WorldAction worldAction;
    private Statistics stats;

    public PlayerDeathListener() {
        stats = new Statistics();
        worldAction = new WorldAction();
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {

        Player player = event.getPlayer();
        World currentWorld = player.getWorld();
        String groupName = Metadata.getWorldGroup(currentWorld);
        String baseName = Metadata.getWorldBaseName(currentWorld);
        stats.incrementAttempts();
        String newWorldGroup = baseName + stats.getAttempts();

        worldAction.createWorldGroup(newWorldGroup,
                Metadata.mapBuilder((plugin, value) -> new WorldMetaData(plugin, value))
                        .put("base-name", baseName)
                        .put("group", newWorldGroup)
                        .put("iteration", stats.getAttempts())
                        .build());

        Bukkit.getScheduler().scheduleSyncDelayedTask(Hardcore.getInstance(),
                () -> worldAction.teleportToWorld(Bukkit.getWorld(newWorldGroup),
                        Bukkit.getServer().getOnlinePlayers()),
                30);

        Bukkit.getScheduler().scheduleSyncDelayedTask(Hardcore.getInstance(), () -> {
            if(groupName.equals("world")) {
                logger.warn("Attempting to unload default worlds, aborting.");
                return;
            }
            worldAction.unloadWorldGroup(groupName);
            worldAction.deleteWorldGroup(groupName);
        }, 80);

    }

}