package fr.theobosse.moddedblocks.api.events;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a block with a persistent data container is placed.
 */
public class PersistentDataBlockPlaceEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    protected boolean cancel;
    protected boolean canBuild;
    protected Block placedAgainst;
    protected BlockState replacedBlockState;
    protected ItemStack itemInHand;
    protected Player player;
    protected EquipmentSlot hand;

    public PersistentDataBlockPlaceEvent(BlockPlaceEvent event) {
        super(event.getBlockPlaced());
        this.placedAgainst = event.getBlockAgainst();
        this.itemInHand = event.getItemInHand();
        this.player = event.getPlayer();
        this.replacedBlockState = event.getBlockReplacedState();
        this.canBuild = event.canBuild();
        this.hand = event.getHand();
        this.cancel = event.isCancelled();
    }

    /**
     * @return if the event is cancelled
     */
    public boolean isCancelled() {
        return this.cancel;
    }

    /**
     * @param cancel if the event should be cancelled
     */
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    /**
     * @return the player who placed the block
     */
    public @NotNull Player getPlayer() {
        return this.player;
    }

    /**
     * @return the block that was placed
     */
    public @NotNull Block getBlockPlaced() {
        return this.getBlock();
    }

    /**
     * @return the state of the block that was replaced
     */
    public @NotNull BlockState getBlockReplacedState() {
        return this.replacedBlockState;
    }

    /**
     * @return the block that was placed against
     */
    public @NotNull Block getBlockAgainst() {
        return this.placedAgainst;
    }

    /**
     * @return the item in the player's hand
     */
    public @NotNull ItemStack getItemInHand() {
        return this.itemInHand;
    }

    /**
     * @return the hand used to place the block
     */
    public @NotNull EquipmentSlot getHand() {
        return this.hand;
    }

    /**
     * @return if the block can be built
     */
    public boolean canBuild() {
        return this.canBuild;
    }

    /**
     * @param canBuild if the block can be built
     */
    public void setBuild(boolean canBuild) {
        this.canBuild = canBuild;
    }

    /**
     * @return the handlers for this event
     */
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    /**
     * @return the handlers for this event
     */
    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }

}
