package studio.magemonkey.fabled.api.entity;

import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Holds block display instances for a target entity.
 */
public class BlockDisplayData {
    private final HashMap<String, BlockDisplayInstance> blockDisplays = new HashMap<>();
    private final LivingEntity target;

    /**
     * @param target target of the block displays
     */
    public BlockDisplayData(LivingEntity target) {
        this.target = target;
    }

    /**
     * @return true if should keep the data, false otherwise
     */
    public boolean isValid() {
        return blockDisplays.size() > 0 && target.isValid();
    }

    /**
     * Fetches an active block display by key
     *
     * @param key block display key
     * @return active block display instance or null if not found
     */
    public BlockDisplayInstance getBlockDisplay(String key) {
        return blockDisplays.get(key);
    }

    /**
     * Registers a block display instance with the given key.
     * If one already exists with that key, it will be removed first.
     *
     * @param blockDisplay the block display instance
     * @param key the key to register under
     */
    public void register(BlockDisplayInstance blockDisplay, String key) {
        BlockDisplayInstance old = blockDisplays.put(key, blockDisplay);
        if (old != null) old.remove();
    }

    /**
     * Ticks each block display for the target
     */
    public void tick() {
        Iterator<BlockDisplayInstance> iterator = blockDisplays.values().iterator();
        while (iterator.hasNext()) {
            BlockDisplayInstance blockDisplay = iterator.next();
            if (blockDisplay.isValid()) {
                blockDisplay.tick();
            } else {
                blockDisplay.remove();
                iterator.remove();
            }
        }
    }

    /**
     * Gets the key for a block display instance
     *
     * @param blockDisplay the instance to find
     * @return the key or null if not found
     */
    public String getKey(BlockDisplayInstance blockDisplay) {
        for (Map.Entry<String, BlockDisplayInstance> entry : blockDisplays.entrySet()) {
            if (entry.getValue() == blockDisplay) return entry.getKey();
        }
        return null;
    }

    /**
     * Removes and unregisters all block displays for this target
     */
    public void remove() {
        blockDisplays.values().forEach(BlockDisplayInstance::remove);
        blockDisplays.clear();
    }

    /**
     * Removes a block display by key
     *
     * @param key the key to remove
     * @return true if a block display was removed, false otherwise
     */
    public boolean remove(String key) {
        BlockDisplayInstance blockDisplay = blockDisplays.get(key);
        if (blockDisplay != null) {
            blockDisplay.remove();
            blockDisplays.remove(key);
            return true;
        }
        return false;
    }

    /**
     * Removes a specific block display instance
     *
     * @param blockDisplay the instance to remove
     */
    public void remove(BlockDisplayInstance blockDisplay) {
        blockDisplay.remove();
        String key = getKey(blockDisplay);
        if (key != null) blockDisplays.remove(key);
    }
}
