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

    public double getChunkChance() {
        return chunkChance;
    }

    public int getDepthMax() {
        return depthMax;
    }

    public int getDepthMin() {
        return depthMin;
    }

    public int getVeinCountMax() {
        return veinCountMax;
    }

    public int getVeinCountMin() {
        return veinCountMin;
    }

    public int getVeinSizeMax() {
        return veinSizeMax;
    }

    public int getVeinSizeMin() {
        return veinSizeMin;
    }

    public List<Biome> getBiomeBlacklist() {
        return biomeBlacklist;
    }

    public List<Biome> getBiomeWhitelist() {
        return biomeWhitelist;
    }

    public List<Material> getBorderingBlacklist() {
        return borderingBlacklist;
    }

    public List<Material> getBorderingWhitelist() {
        return borderingWhitelist;
    }

    public List<Material> getReplaceable() {
        return replaceable;
    }

    public List<String> getWorldBlacklist() {
        return worldBlacklist;
    }

    public List<String> getWorldWhitelist() {
        return worldWhitelist;
    }

    public List<String> getAllowedWorlds() {
        if (!worldWhitelist.isEmpty())
            return worldWhitelist.stream().filter(world -> !worldBlacklist.contains(world)).collect(Collectors.toList());
        List<String> worlds = Bukkit.getWorlds().stream().map(WorldInfo::getName).collect(Collectors.toList());
        worlds.removeAll(worldBlacklist);
        return worlds;
    }

    public boolean isSlimeChunk() {
        return slimeChunk;
    }

    public boolean isWorldAllowed(String world) {
        if (worldBlacklist.contains(world)) return false;
        if (worldWhitelist.isEmpty()) return true;
        return worldWhitelist.contains(world);
    }

    public boolean isBiomeAllowed(Biome biome) {
        if (biomeBlacklist.contains(biome)) return false;
        if (biomeWhitelist.isEmpty()) return true;
        return biomeWhitelist.contains(biome);
    }

    public boolean isBorderingAllowed(Material material) {
        if (borderingBlacklist.contains(material)) return false;
        if (borderingWhitelist.isEmpty()) return true;
        return borderingWhitelist.contains(material);
    }

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

    public boolean isReplaceable(Material material) {
        return replaceable.contains(material) || replaceable.isEmpty();
    }

    public int getVeinSize(Random random) {
        return random.nextInt(veinSizeMax - veinSizeMin + 1) + veinSizeMin;
    }

    public int getVeinCount(Random random) {
        return random.nextInt(veinCountMax - veinCountMin + 1) + veinCountMin;
    }

    public int getDepth(Random random) {
        return random.nextInt(depthMax - depthMin + 1) + depthMin;
    }

    public boolean canGenerateInChunk(Random random) {
        return random.nextDouble() <= chunkChance;
    }

    public boolean canGenerateAtLocation(Location location, LimitedRegion region) {
        if (location == null) return false;
        if (!isBiomeAllowed(location.getBlock().getBiome())) return false;
        if (!isBorderingAllowed(location.getBlock(), region)) return false;
        if (!isReplaceable(region.getType(location))) return false;
        if (slimeChunk && !location.getChunk().isSlimeChunk()) return false;
        return true;
    }
}
