package studio.magemonkey.fabled.api.entity;

import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Holds text display instances for a target entity.
 */
public class TextDisplayData {
    private final HashMap<String, TextDisplayInstance> textDisplays = new HashMap<>();
    private final LivingEntity target;

    /**
     * @param target target of the text displays
     */
    public TextDisplayData(LivingEntity target) {
        this.target = target;
    }

    /**
     * @return true if should keep the data, false otherwise
     */
    public boolean isValid() {
        return textDisplays.size() > 0 && target.isValid();
    }

    /**
     * Fetches an active text display by key
     *
     * @param key text display key
     * @return active text display instance or null if not found
     */
    public TextDisplayInstance getTextDisplay(String key) {
        return textDisplays.get(key);
    }

    /**
     * Registers a text display instance with the given key.
     * If one already exists with that key, it will be removed first.
     *
     * @param textDisplay the text display instance
     * @param key the key to register under
     */
    public void register(TextDisplayInstance textDisplay, String key) {
        TextDisplayInstance old = textDisplays.put(key, textDisplay);
        if (old != null) old.remove();
    }

    /**
     * Ticks each text display for the target
     */
    public void tick() {
        Iterator<TextDisplayInstance> iterator = textDisplays.values().iterator();
        while (iterator.hasNext()) {
            TextDisplayInstance textDisplay = iterator.next();
            if (textDisplay.isValid()) {
                textDisplay.tick();
            } else {
                textDisplay.remove();
                iterator.remove();
            }
        }
    }

    /**
     * Gets the key for a text display instance
     *
     * @param textDisplay the instance to find
     * @return the key or null if not found
     */
    public String getKey(TextDisplayInstance textDisplay) {
        for (Map.Entry<String, TextDisplayInstance> entry : textDisplays.entrySet()) {
            if (entry.getValue() == textDisplay) return entry.getKey();
        }
        return null;
    }

    /**
     * Removes and unregisters all text displays for this target
     */
    public void remove() {
        textDisplays.values().forEach(TextDisplayInstance::remove);
        textDisplays.clear();
    }

    /**
     * Removes a text display by key
     *
     * @param key the key to remove
     * @return true if a text display was removed, false otherwise
     */
    public boolean remove(String key) {
        TextDisplayInstance textDisplay = textDisplays.get(key);
        if (textDisplay != null) {
            textDisplay.remove();
            textDisplays.remove(key);
            return true;
        }
        return false;
    }

    /**
     * Removes a specific text display instance
     *
     * @param textDisplay the instance to remove
     */
    public void remove(TextDisplayInstance textDisplay) {
        textDisplay.remove();
        String key = getKey(textDisplay);
        if (key != null) textDisplays.remove(key);
    }
}
