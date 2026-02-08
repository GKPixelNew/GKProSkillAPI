package studio.magemonkey.fabled.api.entity;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import studio.magemonkey.fabled.Fabled;

/**
 * Represents an active text display entity instance that can follow a target.
 */
public class TextDisplayInstance {
    private static final Vector UP = new Vector(0, 1, 0);
    
    @Getter
    private final TextDisplay textDisplay;
    @Getter
    private final LivingEntity target;
    @Getter @Setter
    private boolean follow;
    @Getter @Setter
    private double forward;
    @Getter @Setter
    private double upward;
    @Getter @Setter
    private double right;
    
    private BukkitTask removalTask;

    public TextDisplayInstance(TextDisplay textDisplay, LivingEntity target, boolean follow) {
        this.textDisplay = textDisplay;
        this.target = target;
        this.follow = follow;
    }

    public TextDisplayInstance(TextDisplay textDisplay, LivingEntity target, boolean follow, double forward, double upward, double right) {
        this.textDisplay = textDisplay;
        this.target = target;
        this.follow = follow;
        this.forward = forward;
        this.upward = upward;
        this.right = right;
    }

    /**
     * Sets the removal task for this instance
     * @param task the scheduled removal task
     */
    public void setRemovalTask(BukkitTask task) {
        this.removalTask = task;
    }

    /**
     * Cancels the current removal task if one exists
     */
    public void cancelRemovalTask() {
        if (removalTask != null && !removalTask.isCancelled()) {
            removalTask.cancel();
            removalTask = null;
        }
    }

    /**
     * Updates all display properties on the text display entity
     */
    public void updateDisplay(Component text, Display.Billboard billboard, Color backgroundColor,
                              byte textOpacity, boolean shadow, boolean seeThrough,
                              int lineWidth, TextDisplay.TextAlignment alignment, double scale,
                              double translateX, double translateY, double translateZ) {
        Runnable update = () -> {
            if (!textDisplay.isValid()) return;
            textDisplay.text(text);
            textDisplay.setBillboard(billboard);
            textDisplay.setBackgroundColor(backgroundColor);
            textDisplay.setTextOpacity(textOpacity);
            textDisplay.setShadowed(shadow);
            textDisplay.setSeeThrough(seeThrough);
            textDisplay.setLineWidth(lineWidth);
            textDisplay.setAlignment(alignment);
            
            if (scale != 1.0 || translateX != 0 || translateY != 0 || translateZ != 0) {
                Transformation transformation = new Transformation(
                        new Vector3f((float) translateX, (float) translateY, (float) translateZ),
                        new AxisAngle4f(0, 0, 0, 1),
                        new Vector3f((float) scale, (float) scale, (float) scale),
                        new AxisAngle4f(0, 0, 0, 1)
                );
                textDisplay.setTransformation(transformation);
            }
        };
        
        if (Bukkit.isPrimaryThread()) {
            update.run();
        } else {
            Bukkit.getScheduler().runTask(Fabled.inst(), update);
        }
    }

    /**
     * Teleports the text display to a new location
     */
    public void teleport(Location loc) {
        Runnable tp = () -> {
            if (textDisplay.isValid()) textDisplay.teleport(loc);
        };
        
        if (Bukkit.isPrimaryThread()) {
            tp.run();
        } else {
            Bukkit.getScheduler().runTask(Fabled.inst(), tp);
        }
    }

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
        cancelRemovalTask();
        Runnable rem = () -> {
            if (textDisplay.isValid()) {
                // Unregister from visibility manager
                VisibilityManager.unregister(textDisplay);
                // Dismount from target if riding
                if (target.isValid() && target.getPassengers().contains(textDisplay)) {
                    target.removePassenger(textDisplay);
                }
                textDisplay.remove();
            }
        };
        
        if (Bukkit.isPrimaryThread()) {
            rem.run();
        } else {
            Bukkit.getScheduler().runTask(Fabled.inst(), rem);
        }
    }

    /**
     * Ticks the text display to update position if following target
     */
    public void tick() {
        if (follow && textDisplay.isValid() && target.isValid()) {
            Runnable tickUpdate = () -> {
                if (!textDisplay.isValid() || !target.isValid()) return;
                
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
            };
            
            if (Bukkit.isPrimaryThread()) {
                tickUpdate.run();
            } else {
                Bukkit.getScheduler().runTask(Fabled.inst(), tickUpdate);
            }
        }
    }
}
