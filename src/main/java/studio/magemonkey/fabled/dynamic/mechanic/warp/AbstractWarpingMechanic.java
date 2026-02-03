/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.mechanic.AbstractWarping
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
package studio.magemonkey.fabled.dynamic.mechanic.warp;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import studio.magemonkey.fabled.dynamic.mechanic.MechanicComponent;
import studio.magemonkey.fabled.dynamic.target.RememberTarget;

import java.util.List;

abstract class AbstractWarpingMechanic extends MechanicComponent {
    protected static final String PRESERVE    = "preserve";
    protected static final String SET_YAW     = "setYaw";
    protected static final String SET_PITCH   = "setPitch";
    protected static final String YAW         = "yaw";
    protected static final String PITCH       = "pitch";
    protected static final String LOOK_AT     = "look-at";
    protected static final String LOOK_AT_KEY = "look-at-key";

    public boolean preserveVelocity() {
        return settings.getBool(PRESERVE, false);
    }

    public boolean setYaw() {
        return settings.getBool(SET_YAW, false);
    }

    public boolean setPitch() {
        return settings.getBool(SET_PITCH, false);
    }

    public boolean relativeYaw() {
        return settings.getBool("relative-yaw", false);
    }

    public boolean relativePitch() {
        return settings.getBool("relative-pitch", false);
    }

    public boolean lookAt() {
        return settings.getBool(LOOK_AT, false);
    }

    public String lookAtKey() {
        return settings.getString(LOOK_AT_KEY, "target");
    }

    public void warp(LivingEntity target, LivingEntity caster, Location location, int level) {
        if (setYaw()) {
            boolean relative = relativeYaw();
            float   yaw      = (float) parseValues(caster, YAW, level, 0);
            if (relative) {
                yaw += target.getLocation().getYaw();
            }
            location.setYaw(yaw);
        }
        if (setPitch()) {
            boolean relative = relativePitch();
            float   pitch    = (float) parseValues(caster, PITCH, level, 0);
            if (relative) {
                pitch += target.getLocation().getPitch();
            }
            location.setPitch(pitch);
        }

        Vector  velocity = target.getVelocity().clone();
        boolean marker   = false;
        if (target instanceof ArmorStand) {
            marker = ((ArmorStand) target).isMarker();
            ((ArmorStand) target).setMarker(false);
        }

        target.teleport(location);

        // Apply look-at rotation AFTER teleport (overrides setYaw/setPitch)
        // This must happen after the initial teleport because mob AI will override
        // rotation when an entity appears at a new location
        if (lookAt()) {
            List<LivingEntity> lookTargets = RememberTarget.remember(caster, lookAtKey());
            if (!lookTargets.isEmpty()) {
                LivingEntity lookTarget = lookTargets.get(0);
                Location lookAt = lookTarget.getLocation().add(0, lookTarget.getEyeHeight() / 2, 0);
                
                // Use the warp destination location, NOT target.getLocation()
                // target.getLocation() may not be updated immediately after teleport
                Location currentLoc = location.clone();
                Vector direction = lookAt.clone().subtract(currentLoc.clone().add(0, target.getEyeHeight(), 0)).toVector();
                
                if (direction.lengthSquared() > 0) {
                    direction.normalize();
                    float newYaw = (float) Math.toDegrees(Math.atan2(-direction.getX(), direction.getZ()));
                    float newPitch = (float) Math.toDegrees(-Math.asin(direction.getY()));
                    currentLoc.setYaw(newYaw);
                    currentLoc.setPitch(newPitch);
                    // Second teleport to force rotation - same technique as LookAtMechanic
                    target.teleport(currentLoc);
                }
            }
        }

        if (preserveVelocity()) {
            target.setVelocity(velocity);
        }

        if (marker) {
            ((ArmorStand) target).setMarker(true);
        }
    }
}
