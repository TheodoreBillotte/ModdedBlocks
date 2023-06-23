package fr.theobosse.moddedblocks.events;

import fr.theobosse.moddedblocks.api.blocks.BlockPersistentData;
import fr.theobosse.moddedblocks.api.events.PersistentDataBlockPlaceEvent;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

public class BlockPlaced implements Listener {

    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent event) {
        Block block = event.getBlock();
        ItemStack item = event.getItemInHand();
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        BlockPersistentData data = new BlockPersistentData(block);
        data.addPersistentDataContainer(container);
        if (container.getKeys().size() == 0)
            return;
        PersistentDataBlockPlaceEvent placeEvent = new PersistentDataBlockPlaceEvent(event);
        placeEvent.callEvent();
        if (placeEvent.isCancelled()) event.setCancelled(true);
    }

}
