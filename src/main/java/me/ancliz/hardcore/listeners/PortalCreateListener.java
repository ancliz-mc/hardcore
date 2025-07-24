package me.ancliz.hardcore.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.PortalCreateEvent;
import me.ancliz.util.logging.Logger;

public class PortalCreateListener implements Listener {
    private final Logger logger = new Logger(this.getClass());
    
    @EventHandler
    public void onPortalCreate(PortalCreateEvent event) { 
        logger.trace("Portal created: {}, {}", event.getReason(), event.getEntity());
    }
    
}