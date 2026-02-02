/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.mechanic.LookAtMechanic
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 MageMonkeyStudio
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package studio.magemonkey.fabled.dynamic.mechanic;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import studio.magemonkey.fabled.dynamic.target.RememberTarget;

import java.util.List;

/**
 * Forces targets to look at a remembered target location
 */
public class LookAtMechanic extends MechanicComponent {
    private static final String SOURCE     = "source";
    private static final String CONTROL    = "control";

    @Override
    public String getKey() {
        return "look at";
    }

    /**
     * Executes the component
     *
     * @param caster  caster of the skill
     * @param level   level of the skill
     * @param targets targets to apply to
     * @param force
     * @return true if applied to something, false otherwise
     */
    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        if (targets.isEmpty()) return false;

        // Get the location to look at from remembered targets
        final List<LivingEntity> sources = RememberTarget.remember(caster, settings.getString(SOURCE, "_none"));
        if (sources.isEmpty()) return false;
        
        final Location lookAt = sources.get(0).getLocation().add(0, sources.get(0).getEyeHeight() / 2, 0);
        final String control = settings.getString(CONTROL, "both").toLowerCase();

        boolean worked = false;
        for (LivingEntity target : targets) {
            Location targetLoc = target.getLocation().clone();
            
            // Calculate direction vector from target's eyes to look-at location
            Vector direction = lookAt.clone().subtract(targetLoc.clone().add(0, target.getEyeHeight(), 0)).toVector();
            
            if (direction.lengthSquared() == 0) {
                continue;
            }
            
            direction.normalize();
            
            // Calculate yaw and pitch from direction vector
            float newYaw = (float) Math.toDegrees(Math.atan2(-direction.getX(), direction.getZ()));
            float newPitch = (float) Math.toDegrees(-Math.asin(direction.getY()));
            
            // Apply based on control setting
            float finalYaw = targetLoc.getYaw();
            float finalPitch = targetLoc.getPitch();
            
            if (control.equals("yaw") || control.equals("both")) {
                finalYaw = newYaw;
            }
            if (control.equals("pitch") || control.equals("both")) {
                finalPitch = newPitch;
            }
            
            // For players, we need to teleport them to change rotation
            // setRotation() doesn't work for players
            if (target instanceof Player player) {
                Location newLoc = player.getLocation().clone();
                newLoc.setYaw(finalYaw);
                newLoc.setPitch(finalPitch);
                player.teleport(newLoc);
            } else if (target instanceof ArmorStand armorStand) {
                // ArmorStands also need teleport to change rotation reliably
                Location newLoc = armorStand.getLocation().clone();
                newLoc.setYaw(finalYaw);
                newLoc.setPitch(finalPitch);
                armorStand.teleport(newLoc);
            } else {
                // For other entities (mobs), setRotation works
                target.setRotation(finalYaw, finalPitch);
            }
            worked = true;
        }
        
        return worked;
    }
}
