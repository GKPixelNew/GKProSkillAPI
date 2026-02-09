package studio.magemonkey.fabled.api.entity;

import org.bukkit.Bukkit;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.LivingEntity;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.listener.MechanicListener;
import studio.magemonkey.fabled.task.BlockDisplayTask;
import studio.magemonkey.fabled.thread.MainThread;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages block display entities created by skills.
 */
public class BlockDisplayManager {
    private static final Map<LivingEntity, BlockDisplayData> instances = new ConcurrentHashMap<>();
    private static boolean initialized = false;

    /**
     * Initializes the block display manager and registers the tick task.
     * Also removes any rogue block displays from previous sessions.
     */
    public static void init() {
        if (initialized) return;
        initialized = true;
        
        MainThread.register(new BlockDisplayTask());
        // Remove any leftover block displays with our metadata
        Bukkit.getWorlds().forEach(world -> world.getEntitiesByClass(BlockDisplay.class).forEach(bd -> {
            if (Fabled.getMeta(bd, MechanicListener.BLOCK_DISPLAY) != null) bd.remove();
        }));
    }

    /**
     * Removes all block display instances
     */
    public static void cleanUp() {
        instances.values().forEach(BlockDisplayData::remove);
        instances.clear();
    }

    /**
     * Clears block displays for a given entity
     *
     * @param target target to clear for
     */
    public static void clear(LivingEntity target) {
        BlockDisplayData data = instances.remove(target);
        if (data != null) data.remove();
    }

    /**
     * Gets the block display data for the given target
     *
     * @param target target to get the data for
     * @return block display data for the target or null if doesn't exist
     */
    public static BlockDisplayData getBlockDisplayData(LivingEntity target) {
        return instances.get(target);
    }

    /**
     * Fetches an active block display for a given target
     *
     * @param target target to get the block display for
     * @param key block display key
     * @return active block display instance or empty if not found
     */
    public static Optional<BlockDisplayInstance> getBlockDisplay(LivingEntity target, String key) {
        if (!instances.containsKey(target)) {
            return Optional.empty();
        }
        return Optional.ofNullable(instances.get(target).getBlockDisplay(key));
    }

    /**
     * Registers an active block display for the given target
     *
     * @param blockDisplay block display instance to register
     * @param target target to register the block display for
     * @param key block display key
     */
    public static void register(BlockDisplayInstance blockDisplay, LivingEntity target, String key) {
        if (!instances.containsKey(target)) {
            instances.put(target, new BlockDisplayData(target));
        }
        instances.get(target).register(blockDisplay, key);
    }

    /**
     * Removes a block display by key for a target
     *
     * @param target the target entity
     * @param key the block display key
     * @return true if a block display was removed, false otherwise
     */
    public static boolean remove(LivingEntity target, String key) {
        BlockDisplayData data = instances.get(target);
        if (data == null) return false;
        
        boolean removed = data.remove(key);
        if (!data.isValid()) {
            instances.remove(target);
        }
        return removed;
    }

    /**
     * Ticks all active block displays
     */
    public static void tick() {
        Iterator<Map.Entry<LivingEntity, BlockDisplayData>> iterator = instances.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<LivingEntity, BlockDisplayData> entry = iterator.next();
            BlockDisplayData data = entry.getValue();
            if (data.isValid()) {
                data.tick();
            } else {
                data.remove();
                iterator.remove();
            }
        }
    }
}
