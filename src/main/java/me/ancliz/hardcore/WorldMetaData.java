package me.ancliz.hardcore;

import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class WorldMetaData implements MetadataValue {
    private final Object value;
    private final Plugin plugin;
    
    public WorldMetaData(Plugin plugin, String value) {
        this.plugin = plugin;
        this.value = value;
    }

    public WorldMetaData(Plugin plugin, int value) {
        this.plugin = plugin;
        this.value = (Integer) value;
    }

    public WorldMetaData(Plugin plugin, Object value) {
        this.plugin = plugin;
        this.value = value;
    }

    @Override
    public Object value() {
        return value;
    }

    @Override
    public int asInt() {
        return (Integer) value;
    }

    @Override
    public float asFloat() {
    return (Float) value;
    }

    @Override
    public double asDouble() {
        return (Double) value;
        }

    @Override
    public long asLong() {
    return (Long) value;
    }

    @Override
    public short asShort() {
    return (Short) value;
    }

    @Override
    public byte asByte() {
    return (Byte) value;
    }

    @Override
    public boolean asBoolean() {
    return (Boolean) value;
    }

    @Override
    public String asString() {
    return (String) value;
    }

    @Override
    public Plugin getOwningPlugin() {
        return plugin;
    }

    @Override
    public void invalidate() {

    }

    @Override
    public String toString() {
        return (String) value;
    }

}