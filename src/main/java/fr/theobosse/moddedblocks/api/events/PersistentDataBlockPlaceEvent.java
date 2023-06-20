package fr.theobosse.moddedblocks.api.events;

import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;

public class PersistentDataBlockPlaceEvent extends BlockPlaceEvent {

    public PersistentDataBlockPlaceEvent(@NotNull BlockPlaceEvent event) {
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
