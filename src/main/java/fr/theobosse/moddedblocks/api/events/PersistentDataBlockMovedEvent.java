package fr.theobosse.moddedblocks.api.events;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a block with a persistent data container is moved by a piston.
 */
public class PersistentDataBlockMovedEvent extends BlockEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled;
    private final BlockFace direction;

    public PersistentDataBlockMovedEvent(@NotNull Block block, @NotNull BlockFace direction) {
        super(block);
        this.direction = direction;
    }

    /**
     * @return the handlers for this event
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * @return if the event is cancelled
     */
    public boolean isCancelled() {
        return this.cancelled;
    }

    /**
     * @param cancelled if the event should be cancelled
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * @return if the piston is sticky
     */
    public boolean isSticky() {
        return this.block.getType() == Material.STICKY_PISTON || this.block.getType() == Material.MOVING_PISTON;
    }

    /**
     * @return the direction the piston is facing
     */
    public @NotNull BlockFace getDirection() {
        return this.direction;
    }
}
