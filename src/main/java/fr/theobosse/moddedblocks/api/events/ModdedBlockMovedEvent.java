package fr.theobosse.moddedblocks.api.events;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockPistonEvent;
import org.jetbrains.annotations.NotNull;

public class ModdedBlockMovedEvent extends BlockPistonEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    public ModdedBlockMovedEvent(@NotNull Block block, @NotNull BlockFace direction) {
        super(block, direction);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
