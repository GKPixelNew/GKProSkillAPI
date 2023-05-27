/**
 * SkillAPI
 * com.sucy.skill.api.event.TrueDamageEvent
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2016 Steven Sucy
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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
package com.sucy.skill.api.event;

import com.sucy.skill.api.skills.Skill;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event for when true damage is dealt (damage ignoring armor)
 */
public class TrueDamageEvent extends DamageEvent {
    private static final HandlerList handlers = new HandlerList();
    private final Skill skill;
    private boolean cancelled;

    /**
     * Initializes a new event
     *
     * @param damager entity dealing the damage
     * @param target  entity receiving the damage
     * @param damage  the amount of damage dealt
     */
    public TrueDamageEvent(Skill skill, LivingEntity damager, LivingEntity target, double damage) {
        super(damager, target, damage, true);
        this.skill = skill;
    }

    /**
     * Retrieves the handlers for the event
     *
     * @return list of event handlers
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * @return skill used to deal the damage
     */
    public Skill getSkill() {
        return skill;
    }

    /**
     * Checks whether the event is cancelled
     *
     * @return true if cancelled, false otherwise
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets the cancelled state of the event
     *
     * @param cancelled the cancelled state of the event
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
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
}
