package studio.magemonkey.fabled.api.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import studio.magemonkey.fabled.dynamic.DynamicSkill;

@RequiredArgsConstructor
public class AccurateLaunchEvent extends Event implements Cancellable {
    private static final HandlerList  handlers  = new HandlerList();
    @Getter
    private final        LivingEntity caster;
    @Getter
    private final        LivingEntity target;
    @Getter
    private final        DynamicSkill skill;
    @Getter
    @Setter
    private              boolean      cancelled = false;

    @Override
    public @NotNull HandlerList getHandlers() {
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
