package me.ancliz.hardcore.listeners;

import org.apache.logging.log4j.LogManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.PortalCreateEvent;
import me.ancliz.hardcore.util.LoggerWrapper;

public class PortalCreateListener implements Listener {
    LoggerWrapper logger = new LoggerWrapper(LogManager.getLogger());
    
    @EventHandler
    public void onPortalCreate(PortalCreateEvent event) { 
        logger.trace("Portal created: {}, {}", event.getReason(), event.getEntity());
    }
    
}