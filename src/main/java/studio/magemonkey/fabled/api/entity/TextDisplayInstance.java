package studio.magemonkey.fabled.api.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Vector;
import studio.magemonkey.fabled.Fabled;

/**
 * Represents an active text display entity instance that can follow a target.
 */
@AllArgsConstructor
@RequiredArgsConstructor
public class TextDisplayInstance {
    private static final Vector UP = new Vector(0, 1, 0);
    
    @Getter
    private final TextDisplay textDisplay;
    private final LivingEntity target;
    private final boolean follow;
    private double forward;
    private double upward;
    private double right;

    /**
     * @return true if the instance is still valid
     */
    public boolean isValid() {
        return target.isValid() && textDisplay.isValid();
    }

    /**
     * Removes the text display
     */
    public void remove() {
        Bukkit.getScheduler().runTask(Fabled.inst(), textDisplay::remove);
    }

    /**
     * Ticks the text display to update position if following target
     */
    public void tick() {
        if (follow) {
            Bukkit.getScheduler().runTask(Fabled.inst(), () -> {
                boolean sameWorld = textDisplay.getWorld().equals(target.getWorld());

                Location loc = target.getLocation().clone();
                Vector dir = loc.getDirection().setY(0).normalize();
                Vector side = dir.clone().crossProduct(UP);
                loc.add(dir.multiply(forward)).add(0, upward, 0).add(side.multiply(right));

                if (!sameWorld) {
                    boolean chunkLoaded = textDisplay.getLocation().getChunk().isLoaded();
                    if (!chunkLoaded) {
                        textDisplay.getLocation().getChunk().load();
                    }
                }
                textDisplay.teleport(loc);
            });
        }
    }
}
