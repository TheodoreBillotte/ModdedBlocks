package fr.theobosse.moddedblocks.api.events;

import fr.theobosse.moddedblocks.api.blocks.CustomBlock;
import org.bukkit.block.Block;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event that's called when a custom block yields experience.
 */
public class CustomBlockExpEvent extends CustomBlockEvent {
    private static final HandlerList handlers = new HandlerList();
    private int exp;

    public CustomBlockExpEvent(@NotNull Block block, @NotNull CustomBlock customBlock, int exp) {
        super(block, customBlock);

        this.exp = exp;
    }

    /**
     * Get the experience dropped by the block after the event has processed
     *
     * @return The experience to drop
     */
    public int getExpToDrop() {
        return exp;
    }

    /**
     * Set the amount of experience dropped by the block after the event has
     * processed
     *
     * @param exp 1 or higher to drop experience, else nothing will drop
     */
    public void setExpToDrop(int exp) {
        this.exp = exp;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
