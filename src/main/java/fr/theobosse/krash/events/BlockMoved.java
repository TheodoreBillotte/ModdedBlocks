package fr.theobosse.krash.events;

import fr.theobosse.krash.api.blocks.BlockPersistentData;
import fr.theobosse.krash.api.events.ModdedBlockMovedEvent;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockMoved implements Listener {

    private void pistonMoveKrashBLock(BlockPistonEvent event, List<Block> blocks) {
        BlockFace direction = event.getDirection();
        HashMap<BlockPersistentData, BlockPersistentData> movedBlocks = new HashMap<>();
        List<BlockPersistentData> removedData = new ArrayList<>();
        boolean clearValue = true;
        int i = 0;

        for (Block block : blocks) {
            BlockPersistentData oldData = new BlockPersistentData(block);
            if (oldData.getValues() == null) {
                clearValue = true;
                continue;
            }
            Block newBlock = block.getRelative(direction);
            BlockPersistentData newData = new BlockPersistentData(newBlock);

            ModdedBlockMovedEvent movedEvent = new ModdedBlockMovedEvent(block, direction);
            Bukkit.getPluginManager().callEvent(movedEvent);
            event.setCancelled(movedEvent.isCancelled());
            if (!event.isCancelled()) {
                movedBlocks.put(oldData, newData);
                if (clearValue) removedData.add(oldData);
            }
            clearValue = false;
        }
        for (Map.Entry<BlockPersistentData, BlockPersistentData> entry : movedBlocks.entrySet()) {
            BlockPersistentData oldData = entry.getKey();
            BlockPersistentData newData = entry.getValue();
            newData.copyFrom(oldData);
        }
        for (BlockPersistentData data : removedData)
            data.clear();
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        pistonMoveKrashBLock(event, event.getBlocks());
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        pistonMoveKrashBLock(event, event.getBlocks());
    }

}
