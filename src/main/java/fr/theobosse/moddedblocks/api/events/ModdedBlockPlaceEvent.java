package fr.theobosse.moddedblocks.api.events;

import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;

public class ModdedBlockPlaceEvent extends BlockPlaceEvent {

    public ModdedBlockPlaceEvent(@NotNull BlockPlaceEvent event) {
        super(
                event.getBlockPlaced(),
                event.getBlockReplacedState(),
                event.getBlockAgainst(),
                event.getItemInHand(),
                event.getPlayer(),
                event.canBuild(),
                event.getHand()
        );
    }

}
