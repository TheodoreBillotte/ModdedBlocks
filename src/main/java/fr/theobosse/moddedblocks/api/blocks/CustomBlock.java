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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A class to manage custom blocks.
 */
public class CustomBlock {

    private int configId = -1;
    private int blockId = -1;
    private MultipleFacing blockData = null;
    private ConfigurationSection section = null;

    /**
     * list of the 192 bytes possibilities for the block id calculation
     */
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

    private CustomBlockData data = null;
    private CustomBlockGenerator generator = null;

    private static final List<CustomBlock> customBlocks = new ArrayList<>();

    private static final HashMap<String, CustomBlockItemRegister> itemRegisters = new HashMap<>();

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
        this.generator = new CustomBlockGenerator(section.getConfigurationSection("generation"));
    }

    /**
     * load all custom blocks from the config
     */
    public static void loadCustomBlocks() {
        YamlConfiguration config = Configs.getConfig("custom-blocks");
        for (String key : config.getKeys(false)) {
            new CustomBlock(Integer.parseInt(key));
        }
    }

    /**
     * calculate the block id from the config id
     */
    public static int calculateBlockId(int configId) {
        int blockId = 0;
        if (configId < 0 || configId > 159) return -1;
        for (int i = 0; i < configId; i++) {
            blockId++;
            while (idCheck[blockId]) blockId++;
        }
        return blockId;
    }

    /**
     * calculate the config id from the block id
     */
    public static int calculateConfigId(int blockId) {
        int configId = 0;
        for (int i = 0; i < blockId; i++) {
            if (idCheck[i]) continue;
            configId++;
        }
        return configId;
    }

    /**
     * register a custom block drop item
     */
    public static void registerItem(String name, CustomBlockItemRegister register) {
        itemRegisters.put(name, register);
    }

    /**
     * unregister a custom block drop item
     */
    public static void unregisterItem(String name) {
        itemRegisters.remove(name);
    }

    /**
     * get the custom block drop item
     */
    public static ItemStack getRegisteredItem(String pluginId, String itemId) {
        CustomBlockItemRegister register = itemRegisters.get(pluginId);
        if (register == null) return null;
        return register.getItemStack(itemId);
    }

    /**
     * calculate the block data from the block id
     */
    public static MultipleFacing calculateBlockData(int blockId) {
        Material material = blockId < 64 ? Material.BROWN_MUSHROOM_BLOCK : blockId < 128 ? Material.RED_MUSHROOM_BLOCK : Material.MUSHROOM_STEM;
        BlockData data = material.createBlockData();
        MultipleFacing blockData = (MultipleFacing) data;
        BlockFace[] faces = {BlockFace.WEST, BlockFace.UP, BlockFace.SOUTH, BlockFace.NORTH, BlockFace.EAST, BlockFace.DOWN};
        for (int i = 0; i < 6; i++)
            blockData.setFace(faces[i], (blockId >> i & 1) == 1);
        return blockData;
    }

    /**
     * calculate the block id from the block
     */
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

    /**
     * check if the block is a mushroom block
     */
    public static boolean isMushroomBlock(@NotNull Block block) {
        return block.getType().equals(Material.BROWN_MUSHROOM_BLOCK) ||
                block.getType().equals(Material.RED_MUSHROOM_BLOCK) ||
                block.getType().equals(Material.MUSHROOM_STEM);
    }

    /**
     * get a list of all custom blocks
     */
    public static List<CustomBlock> getCustomBlocks() {
        return customBlocks;
    }

    /**
     * get a custom block from a block
     */
    public static @Nullable CustomBlock getCustomBlock(Block block) {
        for (CustomBlock customBlock : customBlocks) {
            if (customBlock.blockData.equals(block.getBlockData())) return customBlock;
        }
        return null;
    }

    /**
     * get a custom block from a config id
     */
    @Contract(pure = true)
    public static @Nullable CustomBlock getCustomBlock(int id) {
        for (CustomBlock customBlock : customBlocks) {
            if (customBlock.configId == id) return customBlock;
        }
        return null;
    }

    /**
     * check if the block is a custom block using an optimized way
     */
    public static boolean isCustomBlock(Block block) {
        int id = calculateBlockId(block);
        return id != -1 && !idCheck[id];
    }

    /**
     * get the id check array
     */
    public static boolean[] getIdCheck() {
        return idCheck;
    }

    /**
     * get the config section of the custom block
     */
    public ConfigurationSection getSection() {
        return section;
    }

    /**
     * get the block data of the custom block
     */
    public MultipleFacing getBlockData() {
        return blockData;
    }

    /**
     * get the block id of the custom block
     */
    public int getBlockId() {
        return blockId;
    }

    /**
     * get the config id of the custom block
     */
    public int getConfigId() {
        return configId;
    }

    /**
     * get the custom block data of the custom block
     */
    public CustomBlockData getData() {
        return data;
    }

    /**
     * get the custom block generator of the custom block
     */
    public CustomBlockGenerator getGenerator() {
        return generator;
    }

    /**
     * get the item of the custom block
     */
    public ItemStack asItemStack() {
        ItemStack item = new ItemStack(Material.STONE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(data.getName());
        meta.setLore(data.getLore());
        meta.setCustomModelData(1000 + configId);
        item.setItemMeta(meta);
        return item;
    }
}
