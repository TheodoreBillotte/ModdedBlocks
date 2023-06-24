package fr.theobosse.moddedblocks.api.events;

import fr.theobosse.moddedblocks.api.blocks.CustomBlock;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;

public class CustomBlockBreakEvent extends CustomBlockExpEvent implements Cancellable {

    private final Player player;
    private boolean dropItems;
    private boolean cancel;

    public CustomBlockBreakEvent(@NotNull final Block theBlock, @NotNull final CustomBlock customBlock, int exp, Player player) {
        super(theBlock, customBlock, exp);

        this.player = player;
        this.dropItems = true; // Defaults to dropping items as it normally would
    }

    /**
     * Gets the Player that is breaking the block involved in this event.
     *
     * @return The Player that is breaking the block involved in this event
     */
    @NotNull
    public Player getPlayer() {
        return player;
    }

    /**
     * Sets whether or not the block will attempt to drop items as it normally
     * would.
     *
     * If and only if this is false then {@link CustomBlockDropItemEvent} will not be
     * called after this event.
     *
     * @param dropItems Whether or not the block will attempt to drop items
     */
    public void setDropItems(boolean dropItems) {
        this.dropItems = dropItems;
    }

    /**
     * Gets whether or not the block will attempt to drop items.
     *
     * If and only if this is false then {@link CustomBlockDropItemEvent} will not be
     * called after this event.
     *
     * @return Whether or not the block will attempt to drop items
     */
    public boolean isDropItems() {
        return this.dropItems;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
