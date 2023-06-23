package fr.theobosse.moddedblocks.api.events;

import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a block with a persistent data container starts falling.
 */
public class PersistentDataBlockStartFallingEvent extends BlockEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private final FallingBlock fallingBlock;

    public PersistentDataBlockStartFallingEvent(@NotNull Block theBlock, FallingBlock fallingBlock) {
        super(theBlock);
        this.fallingBlock = fallingBlock;
    }

    /**
     * @return the handlers for this event
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * @return the falling block
     */
    public FallingBlock getFallingBlock() {
        return fallingBlock;
    }
}
