package fr.theobosse.krash.events;

import fr.theobosse.krash.api.blocks.BlockPersistentData;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class BlockListener implements Listener {

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null || !event.getAction().isRightClick() ||
            event.getHand() == EquipmentSlot.OFF_HAND) return;
        Block block = event.getClickedBlock();
        BlockPersistentData data = new BlockPersistentData(block);

        String text = data.get("text", String.class);
        if (text != null)
            event.getPlayer().sendRichMessage(text);
    }

}
