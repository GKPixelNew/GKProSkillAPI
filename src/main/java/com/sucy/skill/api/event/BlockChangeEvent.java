package com.sucy.skill.api.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BlockChangeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private @Getter @Setter BlockState from;
    private @Getter @Setter BlockState to;

    public BlockChangeEvent(BlockState from, BlockState to) {
        this.from = from;
        this.to = to;
        this.cancelled = false;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
