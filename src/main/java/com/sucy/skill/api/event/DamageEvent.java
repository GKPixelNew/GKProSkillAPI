package com.sucy.skill.api.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

/**
 * An event for when an entity is damaged by another entity in this plugin.
 */
@AllArgsConstructor
public abstract class DamageEvent extends Event implements Cancellable {
    /**
     * Retrieves the entity that dealt the damage
     *
     * @return entity that dealt the damage
     */
    @Getter
    private LivingEntity damager;
    /**
     * Retrieves the entity that received the damage
     *
     * @return entity that received the damage
     */
    @Getter
    private LivingEntity target;
    private double damage;
    private boolean cancelled;

    /**
     * Retrieves the amount of damage dealt
     *
     * @return amount of damage dealt
     */
    public double getDamage() {
        return damage;
    }

    /**
     * Sets the amount of damage dealt
     *
     * @param amount amount of damage dealt
     */
    public void setDamage(double amount) {
        damage = amount;
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
}
