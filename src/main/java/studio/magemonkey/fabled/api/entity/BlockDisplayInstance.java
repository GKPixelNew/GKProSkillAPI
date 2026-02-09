package studio.magemonkey.fabled.api.entity;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import studio.magemonkey.fabled.Fabled;

/**
 * Represents an active block display entity instance that can follow a target.
 */
public class BlockDisplayInstance {
    private static final Vector UP = new Vector(0, 1, 0);
    
    @Getter
    private final BlockDisplay blockDisplay;
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

    public BlockDisplayInstance(BlockDisplay blockDisplay, LivingEntity target, boolean follow) {
        this.blockDisplay = blockDisplay;
        this.target = target;
        this.follow = follow;
    }

    public BlockDisplayInstance(BlockDisplay blockDisplay, LivingEntity target, boolean follow, double forward, double upward, double right) {
        this.blockDisplay = blockDisplay;
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
     * Updates all display properties on the block display entity
     */
    public void updateDisplay(BlockData block, Display.Billboard billboard, Display.Brightness brightness,
                              boolean glowing, float viewRange, double scale,
                              double translateX, double translateY, double translateZ,
                              float pitch, float yaw, float roll) {
        Runnable update = () -> {
            if (!blockDisplay.isValid()) return;
            
            blockDisplay.setBlock(block);
            blockDisplay.setBillboard(billboard);
            if (brightness != null) {
                blockDisplay.setBrightness(brightness);
            }
            blockDisplay.setGlowing(glowing);
            blockDisplay.setViewRange(viewRange);
            
            // Create transformation with scale and rotation
            Transformation transformation = createTransformation(scale, translateX, translateY, translateZ, pitch, yaw, roll);
            blockDisplay.setTransformation(transformation);
        };
        
        if (Bukkit.isPrimaryThread()) {
            update.run();
        } else {
            Bukkit.getScheduler().runTask(Fabled.inst(), update);
        }
    }

    /**
     * Creates a transformation with scale, translation, and rotation
     */
    private Transformation createTransformation(double scale, double translateX, double translateY, double translateZ,
                                                  float pitch, float yaw, float roll) {
        Vector3f translation = new Vector3f((float) translateX, (float) translateY, (float) translateZ);
        Vector3f scaleVec = new Vector3f((float) scale, (float) scale, (float) scale);
        
        // Convert euler angles (degrees) to quaternion
        Quaternionf rotation = new Quaternionf().rotateXYZ(
                (float) Math.toRadians(pitch),
                (float) Math.toRadians(yaw),
                (float) Math.toRadians(roll)
        );
        
        return new Transformation(
                translation,
                rotation,
                scaleVec,
                new Quaternionf()
        );
    }

    /**
     * Teleports the block display to a new location
     */
    public void teleport(Location loc) {
        Runnable tp = () -> {
            if (blockDisplay.isValid()) blockDisplay.teleport(loc);
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
        return target.isValid() && blockDisplay.isValid();
    }

    /**
     * Removes the block display
     */
    public void remove() {
        cancelRemovalTask();
        Runnable rem = () -> {
            if (blockDisplay.isValid()) {
                // Unregister from visibility manager
                VisibilityManager.unregister(blockDisplay);
                // Dismount from target if riding
                if (target.isValid() && target.getPassengers().contains(blockDisplay)) {
                    target.removePassenger(blockDisplay);
                }
                blockDisplay.remove();
            }
        };
        
        if (Bukkit.isPrimaryThread()) {
            rem.run();
        } else {
            Bukkit.getScheduler().runTask(Fabled.inst(), rem);
        }
    }

    /**
     * Ticks the block display to update position if following target
     */
    public void tick() {
        if (follow && blockDisplay.isValid() && target.isValid()) {
            Runnable tickUpdate = () -> {
                if (!blockDisplay.isValid() || !target.isValid()) return;
                
                boolean sameWorld = blockDisplay.getWorld().equals(target.getWorld());

                Location loc = target.getLocation().clone();
                Vector dir = loc.getDirection().setY(0).normalize();
                Vector side = dir.clone().crossProduct(UP);
                loc.add(dir.multiply(forward)).add(0, upward, 0).add(side.multiply(right));

                if (!sameWorld) {
                    boolean chunkLoaded = blockDisplay.getLocation().getChunk().isLoaded();
                    if (!chunkLoaded) {
                        blockDisplay.getLocation().getChunk().load();
                    }
                }
                blockDisplay.teleport(loc);
            };
            
            if (Bukkit.isPrimaryThread()) {
                tickUpdate.run();
            } else {
                Bukkit.getScheduler().runTask(Fabled.inst(), tickUpdate);
            }
        }
    }
}
