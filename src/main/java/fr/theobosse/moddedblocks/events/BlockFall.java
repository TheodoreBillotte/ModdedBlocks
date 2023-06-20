package fr.theobosse.moddedblocks.events;

import fr.theobosse.moddedblocks.api.blocks.BlockPersistentData;
import fr.theobosse.moddedblocks.api.events.ModdedBlockFelt;
import fr.theobosse.moddedblocks.api.events.ModdedBlockStartFalling;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.*;

public class BlockFall implements Listener {

    private final HashMap<UUID, fr.theobosse.moddedblocks.tools.BlockFall> FALLING_BLOCKS_VALUES = new HashMap<>();

    @EventHandler
    public void onBlockFall(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof FallingBlock)) return;
        FallingBlock fb = (FallingBlock) entity;
        Block block = fb.getLocation().getBlock();
        BlockPersistentData data = new BlockPersistentData(block);
        if (data.getValues() == null) return;
        FALLING_BLOCKS_VALUES.put(fb.getUniqueId(), new fr.theobosse.moddedblocks.tools.BlockFall(data.getValues(), block.getLocation()));
        data.clear();
        new ModdedBlockStartFalling(block, fb).callEvent();
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (!(event.getEntity() instanceof FallingBlock)) return;
        FallingBlock fb = (FallingBlock) event.getEntity();
        if (!FALLING_BLOCKS_VALUES.containsKey(fb.getUniqueId())) return;
        fr.theobosse.moddedblocks.tools.BlockFall bf = FALLING_BLOCKS_VALUES.get(fb.getUniqueId());
        if (bf == null || bf.getValues() == null || bf.getLocation() == null) return;
        BlockPersistentData data = new BlockPersistentData(event.getBlock());
        data.setValues(bf.getValues());
        FALLING_BLOCKS_VALUES.remove(fb.getUniqueId());
        new ModdedBlockFelt(event.getBlock(), fb, bf.getLocation()).callEvent();
    }

}
