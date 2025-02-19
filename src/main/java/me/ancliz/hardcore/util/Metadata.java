package me.ancliz.hardcore.util;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import org.apache.logging.log4j.LogManager;
import org.bukkit.World;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;
import me.ancliz.hardcore.Hardcore;

public class Metadata {
    private static LoggerWrapper logger = new LoggerWrapper(LogManager.getLogger());
    private static Plugin plugin = Hardcore.getInstance();

    public static String getWorldGroup(World world) {
        return getOrDefault(plugin, world, "group", "world");
    }

    public static String getWorldBaseName(World world) {
        return getOrDefault(plugin, world, "base-name", "world");
    }

    public static Object getMetadata(Plugin plugin, Metadatable object, String key) {
        return object.getMetadata(key).stream()
            .filter(data -> data.getOwningPlugin().equals(plugin))
            .map(MetadataValue::value)
            .findFirst().orElseThrow(() -> new NoSuchElementException());
    }

    @SuppressWarnings("unchecked")
    public static <T> T getOrDefault(Plugin plugin, Metadatable object, String key, T def) {
        try {
            return (T) getMetadata(plugin, object, key);
        } catch(NoSuchElementException e) {
            logger.warn("No metadata '{}' for plugin '{}', defaulting to '{}'", key, plugin, def);
            return def;
        }
    }

    public static <T extends MetadataValue> MapBuilder<T> mapBuilder(BiFunction<Plugin, Object, T> factory) {
        return new MapBuilder<>(factory);
    }

    public static class MapBuilder<T extends MetadataValue> {
        private Map<String, T> map = new HashMap<>();
        private BiFunction<Plugin, Object, T> factory;

        public MapBuilder(BiFunction<Plugin, Object, T> factory) {
            this.factory = factory;
        }

        public MapBuilder<T> put(String key, Object value) {
            map.put(key, factory.apply(plugin, value));
            return this;
        }

        public Map<String, T> build() {
            return map;
        }
    }

}