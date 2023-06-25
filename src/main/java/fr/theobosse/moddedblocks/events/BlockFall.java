package fr.theobosse.moddedblocks.events;

import fr.theobosse.moddedblocks.api.blocks.BlockPersistentData;
import fr.theobosse.moddedblocks.api.events.PersistentDataBlockFeltEvent;
import fr.theobosse.moddedblocks.api.events.PersistentDataBlockStartFallingEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.*;

public class BlockFall implements Listener {

    private final HashMap<UUID, FallingBlockValues> FALLING_BLOCKS_VALUES = new HashMap<>();

    @EventHandler
    public void onBlockFall(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof FallingBlock fb)) return;
        Block block = fb.getLocation().getBlock();
        BlockPersistentData data = new BlockPersistentData(block);
        if (data.getValues() == null) return;
        FALLING_BLOCKS_VALUES.put(fb.getUniqueId(), new FallingBlockValues(data.getValues(), block.getLocation()));
        data.clear();
        PersistentDataBlockStartFallingEvent fallEvent = new PersistentDataBlockStartFallingEvent(block, fb);
        Bukkit.getPluginManager().callEvent(fallEvent);
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (!(event.getEntity() instanceof FallingBlock fb)) return;
        if (!FALLING_BLOCKS_VALUES.containsKey(fb.getUniqueId())) return;
        FallingBlockValues bf = FALLING_BLOCKS_VALUES.get(fb.getUniqueId());
        if (bf == null || bf.getValues() == null || bf.getLocation() == null) return;
        BlockPersistentData data = new BlockPersistentData(event.getBlock());
        data.setValues(bf.getValues());
        FALLING_BLOCKS_VALUES.remove(fb.getUniqueId());
        PersistentDataBlockFeltEvent feltEvent = new PersistentDataBlockFeltEvent(event.getBlock(), fb, bf.getLocation());
        Bukkit.getPluginManager().callEvent(feltEvent);
    }

    private static class FallingBlockValues {

        private final Map<String, Object> values;
        private final Location location;

        public FallingBlockValues(Map<String, Object> values, Location location) {
            this.values = values;
            this.location = location;
        }

        public Location getLocation() {
            return location;
        }

        public Map<String, Object> getValues() {
            return values;
        }
    }

}
