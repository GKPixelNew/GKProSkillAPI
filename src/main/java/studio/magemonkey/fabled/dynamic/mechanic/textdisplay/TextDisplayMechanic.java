package studio.magemonkey.fabled.dynamic.mechanic.textdisplay;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.entity.TextDisplayInstance;
import studio.magemonkey.fabled.api.entity.TextDisplayManager;
import studio.magemonkey.fabled.api.entity.VisibilityManager;
import studio.magemonkey.fabled.dynamic.TempEntity;
import studio.magemonkey.fabled.dynamic.mechanic.MechanicComponent;
import studio.magemonkey.fabled.listener.MechanicListener;
import studio.magemonkey.fabled.task.RemoveEntitiesTask;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Summons a text display entity that can show formatted text. Applies child components on the text display.
 */
public class TextDisplayMechanic extends MechanicComponent {
    private static final Vector UP = new Vector(0, 1, 0);

    private static final String KEY              = "key";
    private static final String DURATION         = "duration";
    private static final String TEXT             = "text";
    private static final String FOLLOW           = "follow";
    private static final String RIDE_TARGET      = "ride-target";
    private static final String REUSE            = "reuse";
    private static final String MARKER           = "marker";
    private static final String BILLBOARD        = "billboard";
    private static final String BACKGROUND_COLOR = "background-color";
    private static final String TEXT_OPACITY     = "text-opacity";
    private static final String SHADOW           = "shadow";
    private static final String SEE_THROUGH      = "see-through";
    private static final String LINE_WIDTH       = "line-width";
    private static final String ALIGNMENT        = "alignment";
    private static final String SCALE            = "scale";
    private static final String FORWARD          = "forward";
    private static final String UPWARD           = "upward";
    private static final String RIGHT            = "right";
    private static final String VISIBILITY       = "visibility";

    @Override
    public String getKey() {
        return "text display";
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        String key        = settings.getString(KEY, skill.getName());
        int    duration   = (int) (20 * parseValues(caster, DURATION, level, 5));
        String text       = settings.getString(TEXT, "Text Display");
        boolean follow    = settings.getBool(FOLLOW, false);
        boolean rideTarget = settings.getBool(RIDE_TARGET, false);
        boolean reuse     = settings.getBool(REUSE, false);
        boolean marker    = settings.getBool(MARKER, true);
        String  billboardStr = settings.getString(BILLBOARD, "center").toUpperCase();
        String  bgColorHex = settings.getString(BACKGROUND_COLOR, "#40000000");
        int     textOpacity = (int) parseValues(caster, TEXT_OPACITY, level, 255);
        boolean shadow    = settings.getBool(SHADOW, false);
        boolean seeThrough = settings.getBool(SEE_THROUGH, false);
        int     lineWidth = (int) parseValues(caster, LINE_WIDTH, level, 200);
        String  alignmentStr = settings.getString(ALIGNMENT, "center").toUpperCase();
        double  scale     = parseValues(caster, SCALE, level, 1.0);
        double  forward   = parseValues(caster, FORWARD, level, 0);
        double  upward    = parseValues(caster, UPWARD, level, 0);
        double  right     = parseValues(caster, RIGHT, level, 0);
        String  visibilityStr = settings.getString(VISIBILITY, "everyone");
        VisibilityManager.VisibilityMode visibilityMode = VisibilityManager.parseMode(visibilityStr);

        // Parse background color from hex
        Color backgroundColor = parseColor(bgColorHex);
        
        // Parse billboard mode
        Display.Billboard billboard;
        try {
            billboard = Display.Billboard.valueOf(billboardStr);
        } catch (IllegalArgumentException e) {
            billboard = Display.Billboard.CENTER;
        }
        final Display.Billboard finalBillboard = billboard;

        // Parse text alignment
        TextDisplay.TextAlignment alignment;
        try {
            alignment = TextDisplay.TextAlignment.valueOf(alignmentStr);
        } catch (IllegalArgumentException e) {
            alignment = TextDisplay.TextAlignment.CENTER;
        }
        final TextDisplay.TextAlignment finalAlignment = alignment;

        // Clamp text opacity
        byte finalOpacity = (byte) Math.max(-128, Math.min(127, textOpacity - 128));

        List<LivingEntity> textDisplays = new ArrayList<>();
        for (LivingEntity target : targets) {
            // Parse text with MiniMessage and placeholders (per target for {target} placeholder)
            Component textComponent = parseText(text, caster, target, level);
            
            Location loc = target.getLocation().clone();
            Vector   dir = loc.getDirection().setY(0).normalize();
            Vector   side = dir.clone().crossProduct(UP);
            double   spawnUpward = rideTarget ? upward + 2 : upward;
            loc.add(dir.multiply(forward)).add(0, spawnUpward, 0).add(side.multiply(right));

            // Check if we should reuse an existing text display
            TextDisplayInstance existing = TextDisplayManager.getTextDisplay(target, key).orElse(null);
            if (reuse && existing != null && existing.isValid()) {
                // Update existing text display instead of creating new
                existing.updateDisplay(textComponent, finalBillboard, backgroundColor,
                        finalOpacity, shadow, seeThrough, lineWidth, finalAlignment, scale);
                existing.teleport(loc);
                existing.setFollow(follow);
                existing.setForward(forward);
                existing.setUpward(upward);
                existing.setRight(right);
                
                // Update visibility
                VisibilityManager.register(existing.getTextDisplay(), caster, visibilityMode);
                
                // Cancel old removal task and schedule new one
                existing.cancelRemovalTask();
                existing.setRemovalTask(Fabled.schedule(() -> {
                    TextDisplayManager.remove(target, key);
                }, duration));
                
                // Wrap in TempEntity for child component execution
                textDisplays.add(new TempEntity(existing.getTextDisplay().getLocation()));
            } else {
                // Create new text display
                Consumer<TextDisplay> onSpawn = td -> {
                    try {
                        td.setPersistent(false);
                    } catch (NoSuchMethodError ignored) {
                    }
                    try {
                        td.setInvulnerable(true);
                    } catch (NoSuchMethodError ignored) {
                    }
                    td.text(textComponent);
                    td.setBillboard(finalBillboard);
                    td.setBackgroundColor(backgroundColor);
                    td.setTextOpacity(finalOpacity);
                    td.setShadowed(shadow);
                    td.setSeeThrough(seeThrough);
                    td.setLineWidth(lineWidth);
                    td.setAlignment(finalAlignment);
                    
                    // Apply scale transformation
                    if (scale != 1.0) {
                        Transformation transformation = new Transformation(
                                new Vector3f(0, 0, 0),
                                new AxisAngle4f(0, 0, 0, 1),
                                new Vector3f((float) scale, (float) scale, (float) scale),
                                new AxisAngle4f(0, 0, 0, 1)
                        );
                        td.setTransformation(transformation);
                    }
                };

                TextDisplay td = target.getWorld().spawn(loc, TextDisplay.class, onSpawn);
                Fabled.setMeta(td, MechanicListener.TEXT_DISPLAY, true);
                
                // Make it a marker (no collision, invisible hitbox)
                if (marker) {
                    td.setViewRange(16.0f);
                }

                TextDisplayInstance instance;
                if (follow) {
                    instance = new TextDisplayInstance(td, target, true, forward, upward, right);
                } else {
                    instance = new TextDisplayInstance(td, target, false);
                }
                TextDisplayManager.register(instance, target, key);

                // Make text display ride on target if enabled
                if (rideTarget) {
                    target.addPassenger(td);
                }

                // Apply visibility restrictions
                VisibilityManager.register(td, caster, visibilityMode);

                // Set up removal task
                instance.setRemovalTask(Fabled.schedule(() -> {
                    TextDisplayManager.remove(target, key);
                }, duration));

                // Wrap in TempEntity for child component execution
                textDisplays.add(new TempEntity(td.getLocation()));
            }
        }
        
        executeChildren(caster, level, textDisplays, force);
        
        return targets.size() > 0;
    }

    /**
     * Parse hex color string to Bukkit Color
     * Supports formats: #RRGGBB, #AARRGGBB, RRGGBB, AARRGGBB
     */
    private Color parseColor(String hex) {
        try {
            hex = hex.replace("#", "");
            if (hex.length() == 6) {
                // RRGGBB format
                int r = Integer.parseInt(hex.substring(0, 2), 16);
                int g = Integer.parseInt(hex.substring(2, 4), 16);
                int b = Integer.parseInt(hex.substring(4, 6), 16);
                return Color.fromRGB(r, g, b);
            } else if (hex.length() == 8) {
                // AARRGGBB format
                int a = Integer.parseInt(hex.substring(0, 2), 16);
                int r = Integer.parseInt(hex.substring(2, 4), 16);
                int g = Integer.parseInt(hex.substring(4, 6), 16);
                int b = Integer.parseInt(hex.substring(6, 8), 16);
                return Color.fromARGB(a, r, g, b);
            }
        } catch (Exception ignored) {
        }
        // Default: semi-transparent black
        return Color.fromARGB(64, 0, 0, 0);
    }

    /**
     * Parse text with MiniMessage and replace placeholders
     */
    private Component parseText(String text, LivingEntity caster, LivingEntity target, int level) {
        // Replace placeholders
        text = text.replace("{caster}", caster.getName())
                   .replace("{target}", target.getName())
                   .replace("{level}", String.valueOf(level));
        
        // If caster is a player, replace additional placeholders
        if (caster instanceof Player player) {
            text = text.replace("{player}", player.getName())
                       .replace("{health}", String.format("%.1f", player.getHealth()))
                       .replace("{max_health}", String.format("%.1f", player.getMaxHealth()));
        }
        
        // Parse with MiniMessage
        return MiniMessage.miniMessage().deserialize(text);
    }

    @Override
    public void playPreview(List<Runnable> onPreviewStop,
                            Player caster,
                            int level,
                            Supplier<List<LivingEntity>> targetSupplier) {
        // Text displays don't have a simple preview
    }
}
