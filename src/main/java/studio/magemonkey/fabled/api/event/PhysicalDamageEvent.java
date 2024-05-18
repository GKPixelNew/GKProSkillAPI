/**
 * Fabled
 * studio.magemonkey.fabled.api.event.PhysicalDamageEvent
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
package studio.magemonkey.fabled.api.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event for when an entity is damaged by another entity without the
 * use of skills such as melee attacks or projectiles.
 */
public class PhysicalDamageEvent extends DamageEvent {
    private static final HandlerList  handlers = new HandlerList();
    private final        boolean      projectile;

    /**
     * Initializes a new event
     *
     * @param damager    entity dealing the damage
     * @param target     entity receiving the damage
     * @param damage     the amount of damage dealt
     * @param projectile whether it was a projectile attack
     */
    public PhysicalDamageEvent(LivingEntity damager, LivingEntity target, double damage, boolean projectile) {
        super(damager, target, damage, false);
        this.projectile = projectile;
    }

    /**
     * Checks whether it was a projectile attack
     *
     * @return true if a projectile attack, false otherwise
     */
    public boolean isProjectile() {
        return projectile;
    }

    /**
     * Retrieves the handlers for the event
     *
     * @return list of event handlers
     */
    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Retrieves the handlers for the event
     *
     * @return list of event handlers
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
