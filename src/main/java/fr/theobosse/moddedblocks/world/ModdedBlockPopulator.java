package fr.theobosse.moddedblocks.world;

import fr.theobosse.moddedblocks.api.blocks.CustomBlock;
import fr.theobosse.moddedblocks.api.blocks.CustomBlockGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ModdedBlockPopulator extends BlockPopulator {

    private final BlockFace faces[] = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    @Override
    public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion) {
        final World world = Bukkit.getWorld(worldInfo.getName());
        CustomBlock.getCustomBlocks().stream().
                filter(block -> block.getGenerator().isWorldAllowed(worldInfo.getName()))
                .filter(block -> block.getGenerator().canGenerateInChunk(random))
                .filter(block -> block.getGenerator().getDepthMin() >= worldInfo.getMinHeight())
                .filter(block -> block.getGenerator().getDepthMax() <= worldInfo.getMaxHeight())
                .forEach(block -> {
                    CustomBlockGenerator generator = block.getGenerator();
                    System.out.println("Generating " + block.getBlockId() + " in " + worldInfo.getName() + " at " + chunkX + " " + chunkZ);
                    for (int i = 0; i < generator.getVeinCount(random); i++) {
                        System.out.println("Generating vein " + i);
                        int x, y, z;
                        boolean isValidPosition = false;
                        Location location = null;
                        if (world == null) return;
                        for (int j = 0; j < 100; j++) {
                            x = chunkX * 16 + random.nextInt(16);
                            z = chunkZ * 16 + random.nextInt(16);
                            y = generator.getDepth(random);
                            if (!limitedRegion.isInRegion(x, y, z)) continue;
                            Chunk chunk = world.getChunkAt(x >> 4, z >> 4);
                            if (!chunk.isLoaded())
                                chunk.load();
                            location = new Location(world, x, y, z);
                            System.out.println("Checking location " + location + " for " + j + " time");
//                            if (!generator.canGenerateAtLocation(location))
//                                continue;
                            System.out.println("Location " + location + " is valid");
                            isValidPosition = true;
                            break;
                        }
                        if (!isValidPosition) break;
                        Block genBlock = location.getBlock();
                        System.out.println("Generating block at " + genBlock.getLocation());

                        for (int j = 0; j < generator.getVeinSize(random); j++) {
                            genBlock.setType(block.getBlockData().getMaterial(), false);
                            genBlock.setBlockData(block.getBlockData(), false);
                            Bukkit.getLogger().log(java.util.logging.Level.INFO, "Generating block at " + genBlock.getLocation());
                            BlockFace[] rndFaces = faces.clone();
                            for (int k = 0; k < rndFaces.length; k++) {
                                int index = random.nextInt(rndFaces.length - k);
                                BlockFace tmp = rndFaces[index + k];
                                rndFaces[index + k] = rndFaces[k];
                                rndFaces[k] = tmp;
                            }
                            for (BlockFace face : rndFaces) {
                                System.out.println("Checking face " + face);
                                Block relative = genBlock.getRelative(face);
                                System.out.println("CRASHED HERE ???");
                                if (relative.getType() == block.getBlockData().getMaterial()) continue;
                                if (generator.isReplaceable(relative.getType())) {
                                    genBlock = relative;
                                    break;
                                }
                            }
                        }
                    }
                });
    }
}
