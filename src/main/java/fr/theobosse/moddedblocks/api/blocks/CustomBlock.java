package fr.theobosse.moddedblocks.api.blocks;

import fr.theobosse.moddedblocks.tools.Configs;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CustomBlock {

    private int configId = -1;
    private int blockId = -1;
    private MultipleFacing blockData = null;
    private ConfigurationSection section = null;
    private static final boolean[] idCheck = {
        true, false, true, true, false, false, true, true, false, false, true, true, false, false, false, false,
        false, false, true, false, false, false, true, false, false, false, true, false, false, false, false, false,
        false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true,
        true, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false,
        true, false, true, false, true, false, true, false, true, false, true, false, false, false, false, false,
        false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true,
        true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false,
        false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true,
    };

    private static final List<CustomBlock> customBlocks = new ArrayList<>();

    private CustomBlockData data = null;

    public CustomBlock(int id) {
        YamlConfiguration config = Configs.getConfig("custom-blocks");
        if (!config.contains(String.valueOf(id))) return;
        this.section = config.getConfigurationSection(String.valueOf(id));
        this.configId = id;
        this.blockId = calculateBlockId(configId);
        if (blockId == -1) return;
        this.blockData = calculateBlockData(blockId);
        customBlocks.add(this);
        this.data = new CustomBlockData(section, this);
    }

    public CustomBlock(Block block) {
        YamlConfiguration config = Configs.getConfig("custom-blocks");
        if (!isMushroomBlock(block)) return;
        this.blockData = (MultipleFacing) block.getBlockData();
        this.blockId = calculateBlockId(block);
        if (blockId == -1 || idCheck[blockId]) {
            blockId = -1;
            return;
        }
        this.configId = calculateConfigId(blockId);
        if (configId == -1) return;
        this.section = config.getConfigurationSection(String.valueOf(configId));
        customBlocks.add(this);
        this.data = new CustomBlockData(section, this);
    }

    public static void loadCustomBlocks() {
        YamlConfiguration config = Configs.getConfig("custom-blocks");
        for (String key : config.getKeys(false)) {
            new CustomBlock(Integer.parseInt(key));
        }
    }

    public static int calculateBlockId(int configId) {
        int blockId = 0;
        if (configId < 0 || configId > 159) return -1;
        for (int i = 0; i < configId; i++) {
            blockId++;
            while (idCheck[blockId]) blockId++;
        }
        return blockId;
    }

    public static int calculateConfigId(int blockId) {
        int configId = 0;
        for (int i = 0; i < blockId; i++) {
            if (idCheck[i]) continue;
            configId++;
        }
        return configId;
    }

    public static MultipleFacing calculateBlockData(int blockId) {
        Material material = blockId < 64 ? Material.BROWN_MUSHROOM_BLOCK : blockId < 128 ? Material.RED_MUSHROOM_BLOCK : Material.MUSHROOM_STEM;
        BlockData data = material.createBlockData();
        MultipleFacing blockData = (MultipleFacing) data;
        BlockFace[] faces = {BlockFace.WEST, BlockFace.UP, BlockFace.SOUTH, BlockFace.NORTH, BlockFace.EAST, BlockFace.DOWN};
        for (int i = 0; i < 6; i++)
            blockData.setFace(faces[i], (blockId >> i & 1) == 1);
        return blockData;
    }

    public static int calculateBlockId(Block block) {
        if (!isMushroomBlock(block)) return -1;
        MultipleFacing blockData = (MultipleFacing) block.getBlockData();
        BlockFace[] faces = {BlockFace.WEST, BlockFace.UP, BlockFace.SOUTH, BlockFace.NORTH, BlockFace.EAST, BlockFace.DOWN};
        int i = 0;
        int blockId = 0;
        for (BlockFace face : faces) {
            if (blockData.getFaces().contains(face)) blockId += 1 << i;
            i++;
        }
        return blockId;
    }

    public static boolean isMushroomBlock(Block block) {
        return block.getType().equals(Material.BROWN_MUSHROOM_BLOCK) ||
                block.getType().equals(Material.RED_MUSHROOM_BLOCK) ||
                block.getType().equals(Material.MUSHROOM_STEM);
    }

    public static List<CustomBlock> getCustomBlocks() {
        return customBlocks;
    }

    public static CustomBlock getCustomBlock(Block block) {
        for (CustomBlock customBlock : customBlocks) {
            if (customBlock.blockData.equals(block.getBlockData())) return customBlock;
        }
        return null;
    }

    public static CustomBlock getCustomBlock(int id) {
        for (CustomBlock customBlock : customBlocks) {
            if (customBlock.configId == id) return customBlock;
        }
        return null;
    }

    public static boolean isCustomBlock(Block block) {
        int id = calculateBlockId(block);
        return id != -1 && !idCheck[id];
    }

    public static boolean[] getIdCheck() {
        return idCheck;
    }

    public ConfigurationSection getSection() {
        return section;
    }

    public MultipleFacing getBlockData() {
        return blockData;
    }

    public int getBlockId() {
        return blockId;
    }

    public int getConfigId() {
        return configId;
    }

    public CustomBlockData getData() {
        return data;
    }

    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.STONE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(data.getName());
        meta.setLore(data.getLore());
        meta.setCustomModelData(1000 + configId);
        item.setItemMeta(meta);
        return item;
    }
}
