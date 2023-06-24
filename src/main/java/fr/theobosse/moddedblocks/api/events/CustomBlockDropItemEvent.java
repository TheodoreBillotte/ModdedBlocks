package fr.theobosse.moddedblocks.api.events;

import fr.theobosse.moddedblocks.api.blocks.CustomBlock;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Called if a custom block broken by a player drops an item.
 * If the block break is cancelled, this event won't be called.
 * If isDropItems in CustomBlockBreakEvent is set to false, this event won't be
 * called.
 * The Block is already broken as this event is called, so #getBlock() will be
 * AIR in most cases. Use #getBlockState() for more Information about the broken
 * block.
 */
public class CustomBlockDropItemEvent extends CustomBlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private boolean cancel;
    private final List<Item> items;

    public CustomBlockDropItemEvent(@NotNull Block block, @NotNull Player player, @NotNull List<Item> items, @NotNull CustomBlock customBlock) {
        super(block, customBlock);
        this.player = player;
        this.items = items;
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
     * Gets list of the Item drops caused by the block break.
     * This list is mutable - removing an item from it will cause it to not
     * drop. Adding to the list is allowed.
     *
     * @return The Item the block caused to drop
     */
    @NotNull
    public List<Item> getItems() {
        return items;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
