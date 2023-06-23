package fr.theobosse.moddedblocks.api.blocks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * A class to manage custom blocks generation.
 */
public class CustomBlockGenerator {

    private final List<Material> replaceable = new ArrayList<>();
    private final List<Material> borderingWhitelist = new ArrayList<>(), borderingBlacklist = new ArrayList<>();
    private final List<Biome> biomeWhitelist = new ArrayList<>(), biomeBlacklist = new ArrayList<>();
    private final List<String> worldWhitelist = new ArrayList<>(), worldBlacklist = new ArrayList<>();
    private final int depthMin, depthMax, veinSizeMin, veinSizeMax, veinCountMin, veinCountMax;
    private final double chunkChance;
    private final boolean slimeChunk;

    final BlockFace[] faces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};


    public CustomBlockGenerator(ConfigurationSection section) {
        if (section == null) {
            depthMin = 0;
            depthMax = 0;
            veinSizeMin = 0;
            veinSizeMax = 0;
            veinCountMin = 0;
            veinCountMax = 0;
            chunkChance = 0;
            slimeChunk = false;
            System.out.println("CustomBlockGenerator: section is null");
            return;
        }
        int[] range = getRange(section.getString("depth", "0"));
        depthMin = range[0];
        depthMax = range[1];
        range = getRange(section.getString("vein-size", "0"));
        veinSizeMin = range[0];
        veinSizeMax = range[1];
        range = getRange(section.getString("vein-count", "0"));
        veinCountMin = range[0];
        veinCountMax = range[1];
        chunkChance = Math.min(Math.max((section.getDouble("chunk-chance", 1)), 0), 1);
        slimeChunk = section.getBoolean("slime-chunk", false);

        section.getStringList("replace").forEach(s -> {
            Material material = Material.getMaterial(s.toUpperCase());
            if (material != null) {
                replaceable.add(material);
            }
        });
        section.getStringList("bordering").forEach(s -> {
            Material material = Material.getMaterial(s.toUpperCase().replace("!", ""));
            if (material != null) {
                (s.startsWith("!") ? borderingBlacklist : borderingWhitelist).add(material);
            }
        });
        section.getStringList("biomes").forEach(s -> {
            Biome biome;
            try {
                biome = Biome.valueOf(s.toUpperCase());
            } catch (IllegalArgumentException e) {
                biome = null;
            }
            if (biome != null) {
                (s.startsWith("!") ? biomeBlacklist : biomeWhitelist).add(biome);
            }
        });
        section.getStringList("worlds").forEach(s -> (s.startsWith("!") ? worldBlacklist : worldWhitelist).add(s.replace("!", "")));
    }

    private int[] getRange(String value) {
        int min = 0, max = 0;
        if (value.contains("~")) {
            String[] split = value.split("~");
            try {
                min = Integer.parseInt(split[0]);
                max = Integer.parseInt(split[1]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return new int[]{min, max};
        } else {
            try {
                min = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return new int[]{min, -1};
        }
    }

    /**
     * @return the chance of a chunk to be generated with this block.
     */
    public double getChunkChance() {
        return chunkChance;
    }

    /**
     * @return the maximum depth of the block.
     */
    public int getDepthMax() {
        return depthMax;
    }

    /**
     * @return the minimum depth of the block.
     */
    public int getDepthMin() {
        return depthMin;
    }

    /**
     * @return the maximum amount of veins per chunk.
     */
    public int getVeinCountMax() {
        return veinCountMax;
    }

    /**
     * @return the minimum amount of veins per chunk.
     */
    public int getVeinCountMin() {
        return veinCountMin;
    }

    /**
     * @return the maximum size of a vein.
     */
    public int getVeinSizeMax() {
        return veinSizeMax;
    }

    /**
     * @return the minimum size of a vein.
     */
    public int getVeinSizeMin() {
        return veinSizeMin;
    }

    /**
     * @return the list of biomes where the block can't be generated.
     */
    public List<Biome> getBiomeBlacklist() {
        return biomeBlacklist;
    }

    /**
     * @return the list of biomes where the block can be generated.
     */
    public List<Biome> getBiomeWhitelist() {
        return biomeWhitelist;
    }

    /**
     * @return the list of blocks that can't border the block.
     */
    public List<Material> getBorderingBlacklist() {
        return borderingBlacklist;
    }

    /**
     * @return the list of blocks that have to border the block.
     */
    public List<Material> getBorderingWhitelist() {
        return borderingWhitelist;
    }

    /**
     * @return the list of blocks that can be replaced by the block.
     */
    public List<Material> getReplaceable() {
        return replaceable;
    }

    /**
     * @return the list of worlds where the block can't be generated.
     */
    public List<String> getWorldBlacklist() {
        return worldBlacklist;
    }

    /**
     * @return the list of worlds where the block can be generated.
     */
    public List<String> getWorldWhitelist() {
        return worldWhitelist;
    }

    /**
     * @return the list of worlds where the block can be generated.
     */
    public List<String> getAllowedWorlds() {
        if (!worldWhitelist.isEmpty())
            return worldWhitelist.stream().filter(world -> !worldBlacklist.contains(world)).collect(Collectors.toList());
        List<String> worlds = Bukkit.getWorlds().stream().map(WorldInfo::getName).collect(Collectors.toList());
        worlds.removeAll(worldBlacklist);
        return worlds;
    }

    /**
     * @return true if the block can be generated in slime chunks.
     */
    public boolean isSlimeChunk() {
        return slimeChunk;
    }

    /**
     * @param world the world to check.
     * @return true if the block can be generated in the given world.
     */
    public boolean isWorldAllowed(String world) {
        if (worldBlacklist.contains(world)) return false;
        if (worldWhitelist.isEmpty()) return true;
        return worldWhitelist.contains(world);
    }

    /**
     * @param biome the biome to check.
     * @return true if the block can be generated in the given biome.
     */
    public boolean isBiomeAllowed(Biome biome) {
        if (biomeBlacklist.contains(biome)) return false;
        if (biomeWhitelist.isEmpty()) return true;
        return biomeWhitelist.contains(biome);
    }

    /**
     * @param material the material of the block to check.
     * @return true if the material is in the bordering whitelist or if the whitelist is empty.
     */
    public boolean isBorderingAllowed(Material material) {
        if (borderingBlacklist.contains(material)) return false;
        if (borderingWhitelist.isEmpty()) return true;
        return borderingWhitelist.contains(material);
    }

    /**
     * @param block the block to check.
     * @param region the region where the block is.
     * @return true if the block is allowed to border the given block.
     */
    public boolean isBorderingAllowed(Block block, LimitedRegion region) {
        for (BlockFace face : faces) {
            int chunkX = block.getX() % 16;
            int chunkZ = block.getZ() % 16;
            if (chunkX == 0 && face == BlockFace.WEST) continue;
            if (chunkX == 15 && face == BlockFace.EAST) continue;
            if (chunkZ == 0 && face == BlockFace.NORTH) continue;
            if (chunkZ == 15 && face == BlockFace.SOUTH) continue;
            if (isBorderingAllowed(region.getType(block.getRelative(face).getLocation())))
                return true;
        }
        return false;
    }

    /**
     * @param material the material of the block to check.
     * @return true if the block can be replaced by the given material.
     */
    public boolean isReplaceable(Material material) {
        return replaceable.contains(material) || replaceable.isEmpty();
    }

    /**
     * @param random the random to use.
     * @return calculate the size of the vein.
     */
    public int getVeinSize(Random random) {
        if (veinSizeMax == -1) return veinSizeMin;
        return random.nextInt(veinSizeMax - veinSizeMin + 1) + veinSizeMin;
    }

    /**
     * @param random the random to use.
     * @return calculate the amount of veins per chunk.
     */
    public int getVeinCount(Random random) {
        if (veinCountMax == -1) return veinCountMin;
        return random.nextInt(veinCountMax - veinCountMin + 1) + veinCountMin;
    }

    /**
     * @param random the random to use.
     * @return calculate the depth of the vein.
     */
    public int getDepth(Random random) {
        if (depthMax == -1) return depthMin;
        return random.nextInt(depthMax - depthMin + 1) + depthMin;
    }

    /**
     * @param random the random to use.
     * @return true if the block can be generated in a chunk.
     */
    public boolean canGenerateInChunk(Random random) {
        return random.nextDouble() <= chunkChance;
    }

    /**
     * @param location the location to check.
     * @param region the region where the block is.
     * @return true if the block can be generated at the given location.
     */
    public boolean canGenerateAtLocation(Location location, LimitedRegion region) {
        if (location == null) return false;
        if (!isBiomeAllowed(location.getBlock().getBiome())) return false;
        if (!isBorderingAllowed(location.getBlock(), region)) return false;
        if (!isReplaceable(region.getType(location))) return false;
        return !slimeChunk || location.getChunk().isSlimeChunk();
    }
}
