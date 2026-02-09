package studio.magemonkey.fabled.dynamic.mechanic.blockdisplay;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.entity.BlockDisplayInstance;
import studio.magemonkey.fabled.api.entity.BlockDisplayManager;
import studio.magemonkey.fabled.api.entity.VisibilityManager;
import studio.magemonkey.fabled.dynamic.TempEntity;
import studio.magemonkey.fabled.dynamic.mechanic.MechanicComponent;
import studio.magemonkey.fabled.listener.MechanicListener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Summons a block display entity that can show a block with customizable properties.
 * Applies child components on the block display.
 */
public class BlockDisplayMechanic extends MechanicComponent {
    private static final Vector UP = new Vector(0, 1, 0);

    private static final String KEY              = "key";
    private static final String DURATION         = "duration";
    private static final String BLOCK            = "block";
    private static final String BLOCK_STATE      = "block-state";
    private static final String ADVANCED_MODE    = "advanced-mode";
    private static final String FOLLOW           = "follow";
    private static final String RIDE_TARGET      = "ride-target";
    private static final String REUSE            = "reuse";
    private static final String MARKER           = "marker";
    private static final String BILLBOARD        = "billboard";
    private static final String SCALE            = "scale";
    private static final String TRANSLATE_X      = "translate-x";
    private static final String TRANSLATE_Y      = "translate-y";
    private static final String TRANSLATE_Z      = "translate-z";
    private static final String PITCH            = "pitch";
    private static final String YAW              = "yaw";
    private static final String ROLL             = "roll";
    private static final String FORWARD          = "forward";
    private static final String UPWARD           = "upward";
    private static final String RIGHT            = "right";
    private static final String VISIBILITY       = "visibility";
    private static final String BLOCK_LIGHT      = "block-light";
    private static final String SKY_LIGHT        = "sky-light";
    private static final String GLOW             = "glow";
    private static final String VIEW_RANGE       = "view-range";

    @Override
    public String getKey() {
        return "block display";
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        String key        = settings.getString(KEY, skill.getName());
        int    duration   = (int) (20 * parseValues(caster, DURATION, level, 5));
        String blockStr   = settings.getString(BLOCK, "STONE");
        String blockState = settings.getString(BLOCK_STATE, "");
        boolean advancedMode = settings.getBool(ADVANCED_MODE, false);
        boolean follow    = settings.getBool(FOLLOW, false);
        boolean rideTarget = settings.getBool(RIDE_TARGET, false);
        boolean reuse     = settings.getBool(REUSE, false);
        boolean marker    = settings.getBool(MARKER, true);
        String  billboardStr = settings.getString(BILLBOARD, "fixed").toUpperCase();
        double  scale     = parseValues(caster, SCALE, level, 1.0);
        double  translateX = parseValues(caster, TRANSLATE_X, level, 0);
        double  translateY = parseValues(caster, TRANSLATE_Y, level, 0);
        double  translateZ = parseValues(caster, TRANSLATE_Z, level, 0);
        float   pitch     = (float) parseValues(caster, PITCH, level, 0);
        float   yaw       = (float) parseValues(caster, YAW, level, 0);
        float   roll      = (float) parseValues(caster, ROLL, level, 0);
        double  forward   = parseValues(caster, FORWARD, level, 0);
        double  upward    = parseValues(caster, UPWARD, level, 0);
        double  right     = parseValues(caster, RIGHT, level, 0);
        String  visibilityStr = settings.getString(VISIBILITY, "everyone");
        VisibilityManager.VisibilityMode visibilityMode = VisibilityManager.parseMode(visibilityStr);
        int     blockLight = (int) parseValues(caster, BLOCK_LIGHT, level, -1);
        int     skyLight  = (int) parseValues(caster, SKY_LIGHT, level, -1);
        boolean glow      = settings.getBool(GLOW, false);
        float   viewRange = (float) parseValues(caster, VIEW_RANGE, level, 1.0);
        
        // Parse block data
        BlockData blockData = parseBlockData(blockStr, blockState, advancedMode);
        if (blockData == null) {
            Fabled.inst().getLogger().warning("Invalid block: " + blockStr);
            return false;
        }
        
        // Parse billboard mode
        Display.Billboard billboard;
        try {
            billboard = Display.Billboard.valueOf(billboardStr);
        } catch (IllegalArgumentException e) {
            billboard = Display.Billboard.FIXED;
        }
        final Display.Billboard finalBillboard = billboard;

        // Parse brightness (only if specified)
        Display.Brightness brightness = null;
        if (blockLight >= 0 || skyLight >= 0) {
            int bl = blockLight >= 0 ? Math.min(15, blockLight) : 0;
            int sl = skyLight >= 0 ? Math.min(15, skyLight) : 15;
            brightness = new Display.Brightness(bl, sl);
        }
        final Display.Brightness finalBrightness = brightness;

        List<LivingEntity> blockDisplays = new ArrayList<>();
        for (LivingEntity target : targets) {
            Location loc = target.getLocation().clone();
            Vector   dir = loc.getDirection().setY(0).normalize();
            Vector   side = dir.clone().crossProduct(UP);
            double   spawnUpward = rideTarget ? upward + 2 : upward;
            loc.add(dir.multiply(forward)).add(0, spawnUpward, 0).add(side.multiply(right));

            // Check if we should reuse an existing block display
            BlockDisplayInstance existing = BlockDisplayManager.getBlockDisplay(target, key).orElse(null);
            if (reuse && existing != null && existing.isValid()) {
                // Update existing block display instead of creating new
                existing.updateDisplay(blockData, finalBillboard, finalBrightness,
                        glow, viewRange, scale, translateX, translateY, translateZ,
                        pitch, yaw, roll);
                
                // Only teleport if not riding or following (those systems handle positioning)
                if (!rideTarget && !follow) {
                    existing.teleport(loc);
                }
                
                existing.setFollow(follow);
                existing.setForward(forward);
                existing.setUpward(upward);
                existing.setRight(right);
                
                // Update visibility
                VisibilityManager.register(existing.getBlockDisplay(), caster, visibilityMode);
                
                // Cancel old removal task and schedule new one
                existing.cancelRemovalTask();
                existing.setRemovalTask(Fabled.schedule(() -> {
                    BlockDisplayManager.remove(target, key);
                }, duration));
                
                // Wrap in TempEntity for child component execution
                blockDisplays.add(new TempEntity(existing.getBlockDisplay().getLocation()));
            } else {
                // Create new block display
                final BlockData finalBlockData = blockData;
                final float finalPitch = pitch;
                final float finalYaw = yaw;
                final float finalRoll = roll;
                
                Consumer<BlockDisplay> onSpawn = bd -> {
                    try {
                        bd.setPersistent(false);
                    } catch (NoSuchMethodError ignored) {
                    }
                    try {
                        bd.setInvulnerable(true);
                    } catch (NoSuchMethodError ignored) {
                    }
                    bd.setBlock(finalBlockData);
                    bd.setBillboard(finalBillboard);
                    if (finalBrightness != null) {
                        bd.setBrightness(finalBrightness);
                    }
                    bd.setGlowing(glow);
                    bd.setViewRange(viewRange);
                    
                    // Apply transformation (scale, translation, and rotation)
                    Transformation transformation = createTransformation(
                            scale, translateX, translateY, translateZ,
                            finalPitch, finalYaw, finalRoll
                    );
                    bd.setTransformation(transformation);
                };

                BlockDisplay bd = target.getWorld().spawn(loc, BlockDisplay.class, onSpawn);
                Fabled.setMeta(bd, MechanicListener.BLOCK_DISPLAY, true);
                
                // Make it a marker (no collision, invisible hitbox)
                if (marker) {
                    bd.setViewRange(viewRange);
                }

                // Apply visibility restrictions via packet interception
                VisibilityManager.register(bd, caster, visibilityMode);

                BlockDisplayInstance instance;
                if (follow) {
                    instance = new BlockDisplayInstance(bd, target, true, forward, upward, right);
                } else {
                    instance = new BlockDisplayInstance(bd, target, false);
                }
                BlockDisplayManager.register(instance, target, key);

                // Make block display ride on target if enabled
                if (rideTarget) {
                    target.addPassenger(bd);
                }

                // Set up removal task
                instance.setRemovalTask(Fabled.schedule(() -> {
                    BlockDisplayManager.remove(target, key);
                }, duration));

                // Wrap in TempEntity for child component execution
                blockDisplays.add(new TempEntity(bd.getLocation()));
            }
        }
        
        executeChildren(caster, level, blockDisplays, force);
        
        return targets.size() > 0;
    }

    /**
     * Parse block data from string
     */
    private BlockData parseBlockData(String blockStr, String blockState, boolean advancedMode) {
        try {
            if (advancedMode && blockState != null && !blockState.isEmpty()) {
                // Advanced mode: use full block state string
                // Format: material[state1=value1,state2=value2]
                String fullBlock = blockStr.toLowerCase();
                if (!blockState.isEmpty()) {
                    fullBlock = blockStr.toLowerCase() + "[" + blockState + "]";
                }
                return Bukkit.createBlockData(fullBlock);
            } else {
                // Simple mode: just material, optionally with state properties
                Material material = Material.matchMaterial(blockStr);
                if (material == null || !material.isBlock()) {
                    return null;
                }
                
                BlockData data = material.createBlockData();
                
                // Apply block state if provided (for dynamic dropdowns)
                if (blockState != null && !blockState.isEmpty()) {
                    try {
                        String fullBlock = material.getKey().getKey() + "[" + blockState + "]";
                        data = Bukkit.createBlockData(fullBlock);
                    } catch (Exception e) {
                        // If state parsing fails, use default block data
                    }
                }
                
                return data;
            }
        } catch (Exception e) {
            return null;
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

    @Override
    public void playPreview(List<Runnable> onPreviewStop, Player caster, int level, List<LivingEntity> targets) {
        // Block displays don't have a simple preview
    }
}
