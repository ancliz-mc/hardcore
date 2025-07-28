package me.ancliz.hardcore.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import me.ancliz.minecraft.metadata.Metadata;
import me.ancliz.util.logging.Logger;

public class PlayerPortalListener implements Listener {
    private final Logger logger = new Logger(this.getClass());

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        TeleportCause cause = event.getCause();
        World from = event.getFrom().getWorld();
        String worldGroup = Metadata.getWorldGroup(from);
        logger.trace("{} from {} to group {}", event.getPlayer().getName(), from.getName(), worldGroup);
        logger.trace("from : {}", from);
        Location location = event.getFrom().clone();
        logger.trace("location.world : {}", location.getWorld());
        if(cause == TeleportCause.NETHER_PORTAL) {

            if(from.getEnvironment() == World.Environment.NORMAL) {
                location.setX(location.getX() / 8);
                location.setZ(location.getZ() / 8);
                location.setWorld(Bukkit.getWorld(worldGroup + "_nether"));
            } else if(from.getEnvironment() == World.Environment.NETHER) {
                location.setX(location.getX() * 8);
                location.setZ(location.getZ() * 8);
                location.setWorld(Bukkit.getWorld(worldGroup));
            }
        } else if(cause == TeleportCause.END_PORTAL) {
            logger.info("Player entered End portal {}", cause);
            location.set(100.5, 49, 0.5);
            location.setWorld(Bukkit.getWorld(worldGroup + "_the_end"));
        }

        logger.debug("location {}, to before: {}", location, event.getTo());
        event.setTo(location);
    }

}