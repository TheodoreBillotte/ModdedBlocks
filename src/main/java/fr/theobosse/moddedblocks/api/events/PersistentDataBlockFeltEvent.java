package fr.theobosse.moddedblocks.api.events;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.jetbrains.annotations.NotNull;

public class PersistentDataBlockFeltEvent extends BlockEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private final FallingBlock fallingBlock;
    private final Location previousLocation;

    public PersistentDataBlockFeltEvent(@NotNull Block theBlock, FallingBlock fallingBlock, Location previousLocation) {
        super(theBlock);
        this.fallingBlock = fallingBlock;
        this.previousLocation = previousLocation;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public FallingBlock getFallingBlock() {
        return fallingBlock;
    }

    public Location getPreviousLocation() {
        return previousLocation;
    }
}
