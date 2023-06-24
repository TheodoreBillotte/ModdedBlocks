package fr.theobosse.moddedblocks.api.events;

import fr.theobosse.moddedblocks.api.blocks.CustomBlock;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a block related event.
 */
public abstract class CustomBlockEvent extends Event {
    private final CustomBlock customBlock;
    protected Block block;

    public CustomBlockEvent(@NotNull final Block theBlock, @NotNull final CustomBlock customBlock) {
        block = theBlock;
        this.customBlock = customBlock;
    }

    /**
     * Gets the block involved in this event.
     *
     * @return The Block which block is involved in this event
     */
    @NotNull
    public final Block getBlock() {
        return block;
    }

    /**
     * Gets the CustomBlock that is involved in this event.
     *
     * @return The CustomBlock that is involved in this event
     */
    @NotNull
    public final CustomBlock getCustomBlock() {
        return customBlock;
    }
}
