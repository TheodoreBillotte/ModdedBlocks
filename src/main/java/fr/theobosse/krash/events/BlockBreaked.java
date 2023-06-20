package fr.theobosse.krash.events;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import fr.theobosse.krash.api.blocks.BlockPersistentData;
import fr.theobosse.krash.api.events.ModdedBlockDestroyedEvent;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;

public class BlockBreaked implements Listener {

    @EventHandler
    public void onBlockBreaked(BlockBreakEvent event) {
        Block block = event.getBlock();
        destroyBlockFunction(block, event);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        for (Block block : event.blockList()) {
            destroyBlockFunction(block, event);
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        for (Block block : event.blockList()) {
            destroyBlockFunction(block, event);
        }
    }

    @EventHandler
    public void onBlockBurned(BlockBurnEvent event) {
        Block block = event.getBlock();
        destroyBlockFunction(block, event);
    }

    @EventHandler
    public void onBlockDestroyed(BlockDestroyEvent event) {
        Block block = event.getBlock();
        destroyBlockFunction(block, event);
    }

    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof TNTPrimed) {
            Block block = entity.getLocation().getBlock();
            destroyBlockFunction(block, event);
        }
    }

    @EventHandler
    public void onLeaveDecay(LeavesDecayEvent event) {
        Block block = event.getBlock();
        destroyBlockFunction(block, event);
    }

    private void destroyBlockFunction(Block block, Cancellable event) {
        ModdedBlockDestroyedEvent destroyEvent = new ModdedBlockDestroyedEvent(block);
        Bukkit.getPluginManager().callEvent(destroyEvent);
        if (destroyEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }
        BlockPersistentData data = new BlockPersistentData(block);
        if (data.getValues() == null) return;
        data.clear();
    }

}
