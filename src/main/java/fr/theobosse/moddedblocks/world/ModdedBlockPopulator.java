package fr.theobosse.moddedblocks.world;

import fr.theobosse.moddedblocks.ModdedBlocks;
import fr.theobosse.moddedblocks.api.blocks.CustomBlock;
import fr.theobosse.moddedblocks.api.blocks.CustomBlockGenerator;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ModdedBlockPopulator extends BlockPopulator {

    private final BlockFace[] faces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};

    @Override
    public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion) {
        final World world = ModdedBlocks.getInstance().getServer().getWorld(worldInfo.getName());
        if (world == null) return;
        CustomBlock.getCustomBlocks().stream().
                filter(block -> block.getGenerator().isWorldAllowed(worldInfo.getName()))
                .filter(block -> block.getGenerator().canGenerateInChunk(random))
                .filter(block -> block.getGenerator().getDepthMin() >= worldInfo.getMinHeight())
                .filter(block -> block.getGenerator().getDepthMax() <= worldInfo.getMaxHeight())
                .forEach(block -> {
                    CustomBlockGenerator generator = block.getGenerator();
                    for (int i = 0; i < generator.getVeinCount(random); i++) {
                        int x, y, z;
                        boolean isValidPosition = false;
                        Location location = null;
                        for (int j = 0; j < 100; j++) {
                            x = chunkX * 16 + random.nextInt(16);
                            z = chunkZ * 16 + random.nextInt(16);
                            y = generator.getDepth(random);
                            if (!limitedRegion.isInRegion(x, y, z)) continue;
                            location = new Location(world, x, y, z);
                            if (!generator.canGenerateAtLocation(location, limitedRegion))
                                continue;
                            isValidPosition = true;
                            break;
                        }
                        if (!isValidPosition) break;

                        int veinSize = generator.getVeinSize(random);
//                        Bukkit.getLogger().log(java.util.logging.Level.INFO, "Generating vein at /tp " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ() + " With size " + veinSize);
                        for (int j = 0; j < veinSize; j++) {
                            limitedRegion.setType(location, block.getBlockData().getMaterial());
                            limitedRegion.setBlockData(location, block.getBlockData());
                            BlockFace[] rndFaces = faces.clone();
                            for (int k = 0; k < rndFaces.length; k++) {
                                int index = random.nextInt(rndFaces.length - k);
                                BlockFace tmp = rndFaces[index + k];
                                rndFaces[index + k] = rndFaces[k];
                                rndFaces[k] = tmp;
                            }
                            for (BlockFace face : rndFaces) {
                                Location relative = location.getBlock().getRelative(face).getLocation();
                                if (!limitedRegion.isInRegion(relative)) continue;
                                if (limitedRegion.getType(relative) == block.getBlockData().getMaterial()) continue;
                                if (generator.isReplaceable(limitedRegion.getType(relative))) {
                                    location = relative;
                                    break;
                                }
                            }
                        }
                    }
                });
    }
}
