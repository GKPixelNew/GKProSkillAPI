package studio.magemonkey.fabled.api.entity;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TextDisplay;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.listener.MechanicListener;
import studio.magemonkey.fabled.task.TextDisplayTask;
import studio.magemonkey.fabled.thread.MainThread;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages text display entities created by skills.
 */
public class TextDisplayManager {
    private static final Map<LivingEntity, TextDisplayData> instances = new ConcurrentHashMap<>();
    private static boolean initialized = false;

    /**
     * Initializes the text display manager and registers the tick task.
     * Also removes any rogue text displays from previous sessions.
     */
    public static void init() {
        if (initialized) return;
        initialized = true;
        
        MainThread.register(new TextDisplayTask());
        // Remove any leftover text displays with our metadata
        Bukkit.getWorlds().forEach(world -> world.getEntitiesByClass(TextDisplay.class).forEach(td -> {
            if (Fabled.getMeta(td, MechanicListener.TEXT_DISPLAY) != null) td.remove();
        }));
    }

    /**
     * Removes all text display instances
     */
    public static void cleanUp() {
        instances.values().forEach(TextDisplayData::remove);
        instances.clear();
    }

    /**
     * Clears text displays for a given entity
     *
     * @param target target to clear for
     */
    public static void clear(LivingEntity target) {
        TextDisplayData data = instances.remove(target);
        if (data != null) data.remove();
    }

    /**
     * Gets the text display data for the given target
     *
     * @param target target to get the data for
     * @return text display data for the target or null if doesn't exist
     */
    public static TextDisplayData getTextDisplayData(LivingEntity target) {
        return instances.get(target);
    }

    /**
     * Fetches an active text display for a given target
     *
     * @param target target to get the text display for
     * @param key text display key
     * @return active text display instance or empty if not found
     */
    public static Optional<TextDisplayInstance> getTextDisplay(LivingEntity target, String key) {
        if (!instances.containsKey(target)) {
            return Optional.empty();
        }
        return Optional.ofNullable(instances.get(target).getTextDisplay(key));
    }

    /**
     * Registers an active text display for the given target
     *
     * @param textDisplay text display instance to register
     * @param target target to register the text display for
     * @param key text display key
     */
    public static void register(TextDisplayInstance textDisplay, LivingEntity target, String key) {
        if (!instances.containsKey(target)) {
            instances.put(target, new TextDisplayData(target));
        }
        instances.get(target).register(textDisplay, key);
    }

    /**
     * Removes a text display by key for a target
     *
     * @param target the target entity
     * @param key the text display key
     * @return true if a text display was removed, false otherwise
     */
    public static boolean remove(LivingEntity target, String key) {
        TextDisplayData data = instances.get(target);
        if (data != null) {
            return data.remove(key);
        }
        return false;
    }

    /**
     * Ticks all active text displays
     */
    public static void tick() {
        Iterator<TextDisplayData> iterator = instances.values().iterator();
        while (iterator.hasNext()) {
            TextDisplayData data = iterator.next();
            if (data.isValid()) {
                data.tick();
            } else {
                data.remove();
                iterator.remove();
            }
        }
    }
}
