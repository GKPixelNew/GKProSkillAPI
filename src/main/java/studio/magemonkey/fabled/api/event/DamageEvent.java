package studio.magemonkey.fabled.api.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * An event for when an entity is damaged by another entity in this plugin.
 */
@AllArgsConstructor
public abstract class DamageEvent extends Event implements Cancellable {
    /**
     * Retrieves the entity that dealt the damage
     */
    @Getter
    private LivingEntity damager;
    /**
     * Retrieves the entity that received the damage
     */
    @Getter
    private LivingEntity target;
    /**
     * -- SETTER --
     * Sets the amount of damage dealt
     * <p>
     * <p>
     * -- GETTER --
     * Retrieves the amount of damage dealt
     */
    @Getter
    @Setter
    private double damage;
    private boolean cancelled;
    @Getter
    private static final HandlerList handlerList = new HandlerList();

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
