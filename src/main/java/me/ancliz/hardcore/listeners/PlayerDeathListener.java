package me.ancliz.hardcore.listeners;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import me.ancliz.hardcore.Hardcore;
import me.ancliz.hardcore.actions.WorldAction;
import me.ancliz.minecraft.AnkyPlugin;
import me.ancliz.minecraft.metadata.Metadata;
import me.ancliz.minecraft.metadata.WorldMetadata;
import me.ancliz.util.logging.Logger;

public class PlayerDeathListener implements Listener {
    private final Logger logger = new Logger(this.getClass());
    private WorldAction worldAction;
    private YamlConfiguration statistics;
    private AnkyPlugin plugin;

    public PlayerDeathListener() {
        plugin = Hardcore.getInstance();
        statistics = plugin.getYaml("statistics.yml", false);
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
                Metadata.mapBuilder((plugin, value) ->
                            new WorldMetadata(plugin, value))
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

            if(!worldAction.deleteWorldGroup(groupName)) {
                logger.info("World deletion disabled, world files will be kept.");
            }

        }, config.getLong("world-delete-delay"));

        plugin.saveYaml(statistics, "statistics.yml");
        
        // Properties properties = new Properties();
        // try {
        //     properties.load(new FileInputStream("server.properties"));
        //     properties.setProperty("level-name", newWorldGroup);
        //     properties.store(new FileOutputStream("server.properties"), newWorldGroup);
        // } catch(IOException e) {
        //     e.printStackTrace();
        // }
    }

}