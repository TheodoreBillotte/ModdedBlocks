package fr.theobosse.moddedblocks.api.events;

import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a block with a persistent data container is destroyed.
 */
public class PersistentDataBlockDestroyedEvent extends BlockEvent implements Cancellable {

    private boolean cancelled = false;
    private static final HandlerList HANDLERS = new HandlerList();

    public PersistentDataBlockDestroyedEvent(@NotNull Block theBlock) {
        super(theBlock);
    }

    /**
     * @return if the event is cancelled
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * @param cancel if the event should be cancelled
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    /**
     * @return the handlers for this event
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
