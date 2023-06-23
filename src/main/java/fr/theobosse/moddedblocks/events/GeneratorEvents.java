package fr.theobosse.moddedblocks.events;

import fr.theobosse.moddedblocks.world.ModdedBlockPopulator;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class GeneratorEvents implements Listener {

//    @EventHandler
//    public void onWorldLoad(WorldLoadEvent event) {
//        World world = event.getWorld();
//        if (world.getPopulators().stream().noneMatch(populator -> populator instanceof ModdedBlockPopulator))
//            world.getPopulators().add(new ModdedBlockPopulator());
//    }

    @EventHandler void onChunkLoad(ChunkLoadEvent event) {
        final World world = event.getWorld();
        if (!event.isNewChunk() || world.getPopulators().stream().anyMatch(populator -> populator instanceof ModdedBlockPopulator))
            return;
        world.getPopulators().add(new ModdedBlockPopulator());
    }

}
