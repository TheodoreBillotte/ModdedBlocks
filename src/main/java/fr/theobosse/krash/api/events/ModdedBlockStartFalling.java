package fr.theobosse.krash.api.events;

import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.jetbrains.annotations.NotNull;

public class ModdedBlockStartFalling extends BlockEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private final FallingBlock fallingBlock;

    public ModdedBlockStartFalling(@NotNull Block theBlock, FallingBlock fallingBlock) {
        super(theBlock);
        this.fallingBlock = fallingBlock;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public FallingBlock getFallingBlock() {
        return fallingBlock;
    }
}
