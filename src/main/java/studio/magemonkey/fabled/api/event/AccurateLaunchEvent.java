package studio.magemonkey.fabled.api.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class AccurateLaunchEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    @Getter
    @Setter
    private boolean cancelled = false;
    @Getter
    private final LivingEntity target;

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean cancel) {

    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return null;
    }
}
