package fr.theobosse.moddedblocks.api.events;

import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.jetbrains.annotations.NotNull;

public class ModdedBlockDestroyedEvent extends BlockEvent implements Cancellable {

    private boolean cancelled = false;
    private static final HandlerList HANDLERS = new HandlerList();

    public ModdedBlockDestroyedEvent(@NotNull Block theBlock) {
        super(theBlock);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
