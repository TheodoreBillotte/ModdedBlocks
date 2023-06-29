package fr.theobosse.moddedblocks.api.blocks;

import fr.theobosse.moddedblocks.managers.DigManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * A class to manage custom blocks data.
 */
public class CustomBlockData {

    private final ConfigurationSection section;
    private final CustomBlockProperties preperties;
    private final int toolLevel;
    private final String tooltype;
    private final double hardness;
    private final double blastResistance;
    private final String name;
    private final List<String> lore;
    private final CustomBlock customBlock;

    public CustomBlockData(ConfigurationSection section, CustomBlock block) {
        this.section = section;
        this.toolLevel = section.getInt("tools.harvest-level");
        this.tooltype = section.getString("tools.type");
        this.hardness = section.getDouble("tools.hardness");
        this.blastResistance = section.getDouble("tools.blast-resistance");
        this.name = section.getString("name");
        this.lore = section.getStringList("lore");
        this.customBlock = block;
        this.preperties = new CustomBlockProperties(section.getConfigurationSection("properties"));
    }

    /**
     * @return configuration section of the custom block
     */
    public ConfigurationSection getSection() {
        return section;
    }

    /**
     * @return the custom block blast resistance
     */
    public double getBlastResistance() {
        return blastResistance;
    }

    /**
     * @return the custom block hardness
     */
    public double getHardness() {
        return hardness;
    }

    /**
     * @return the custom block tool level required to break the block
     */
    public int getToolLevel() {
        return toolLevel;
    }

    /**
     * @return the tool type used to break the custom block
     */
    public String getTooltype() {
        return tooltype;
    }

    /**
     * @return the custom block lore for the item of the block
     */
    public List<String> getLore() {
        return lore;
    }

    /**
     * @return the custom block name for the item of the block
     */
    public String getName() {
        return name;
    }

    /**
     * @return the custom block him self
     */
    public CustomBlock getCustomBlock() {
        return customBlock;
    }

    public long getToolSpeed(ItemStack tool) {
        if (!section.contains("tools.type")) return 0;
        String type = section.getString("tools.type");
        DigManager.ToolType toolType = DigManager.ToolType.getToolType(tool);
        DigManager.ToolType blockType = DigManager.ToolType.getToolType(type);
        if (toolType != blockType) return 0;
        if (toolType == DigManager.ToolType.OTHER) return 0;
        DigManager.ToolMaterial toolMaterial = DigManager.ToolMaterial.getToolMaterial(tool);
        return toolMaterial.getSpeed();
    }

    /**
     * @param tool the tool used to break the block
     * @return the time in tick to break the block
     */
    public long getBreakTime(ItemStack tool) {
        if (tool == null) return -1;
        double speedModifier;
        boolean validTool = isValidTool(tool, false);
        if (validTool) {
            speedModifier = getToolSpeed(tool);
            if (tool.containsEnchantment(org.bukkit.enchantments.Enchantment.DIG_SPEED))
                speedModifier += Math.pow(tool.getEnchantmentLevel(org.bukkit.enchantments.Enchantment.DIG_SPEED), 2) + 1;
        } else speedModifier = 1;

        double damage = speedModifier / hardness;
        if (validTool) damage /= 30;
        else damage /= 100;
        if (damage > 1) damage = 1;
        return (long) Math.ceil(1 / damage);
    }

    /**
     * @param tool the tool to check
     * @param checkMaterial if the tool material should be checked
     * @return if the tool is valid to break the block
     */
    public boolean isValidTool(ItemStack tool, boolean checkMaterial) {
        if (!section.contains("tools.type")) return true;
        String type = section.getString("tools.type");
        DigManager.ToolType toolType = DigManager.ToolType.getToolType(tool);
        DigManager.ToolType blockType = DigManager.ToolType.getToolType(type);
        if (!checkMaterial) return toolType == blockType;
        DigManager.ToolMaterial toolMaterial = DigManager.ToolMaterial.getToolMaterial(tool);
        return toolMaterial.getLevel() >= toolLevel && toolType == blockType;
    }

    /**
     * @param tool the tool to check
     * @return if the tool is valid to break the block
     */
    public boolean isValidTool(ItemStack tool) {
        return isValidTool(tool, true);
    }

    /**
     * @param rolls the number of rolls to get the drops
     * @return the drops of the block with the number of rolls
     */
    public List<ItemStack> getDrops(int rolls) {
        Random random = new Random();
        ConfigurationSection lootSection = section.getConfigurationSection("loots.items");
        List<ItemStack> items = new ArrayList<>();

        if (lootSection == null) return items;
        for (String key : lootSection.getKeys(false)) {
            int chance = 100;
            int amount;
            String loot = lootSection.getString(key);
            if (loot == null) continue;

            if (loot.contains("%")) {
                String[] values = loot.split("%");
                chance = Integer.parseInt(values[1]);
                amount = Integer.parseInt(values[0]);
            } else amount = Integer.parseInt(loot);

            int dropAmount = 0;
            for (int i = 0; i < rolls; i++)
                if (random.nextInt(100) < chance)
                    dropAmount += amount;

            if (key.contains(":")) {
                String[] values = key.split(":");
                String pluginId = values[0];
                String id = values[1];
                ItemStack item = CustomBlock.getRegisteredItem(pluginId, id);
                if (item == null) continue;
                item.setAmount(dropAmount);
                items.add(item);
            } else {
                Material material = Material.getMaterial(key);
                if (material == null) continue;
                items.add(new ItemStack(material, dropAmount));
            }
        }
        return items;
    }

    /**
     * @return the drops of the block
     */
    public List<ItemStack> getDrops() {
        return getDrops(1);
    }

    /**
     * @param tool the tool used to break the block
     * @return the drops of the block with the tool
     */
    public List<ItemStack> getDrops(ItemStack tool) {
        boolean validTool = isValidTool(tool);
        if (!validTool && preperties.isLootNeedTool()) return new ArrayList<>();
        if (!validTool) return getDrops();
        if (preperties.isSilkTouchable() && tool.containsEnchantment(org.bukkit.enchantments.Enchantment.SILK_TOUCH))
            return Collections.singletonList(customBlock.asItemStack());
        if (!preperties.isFortunate()) return getDrops();
        int rolls = 1;
        int looting = tool.getEnchantmentLevel(org.bukkit.enchantments.Enchantment.LOOT_BONUS_BLOCKS);
        Random random = new Random();
        for (int i = 0; i < looting; i++)
            if (random.nextInt(100) <= 33) rolls++;
        return getDrops(rolls);
    }

    /**
     * @return the exp drop of the block
     */
    public int getExpDrop() {
        String form = section.getString("loots.xp");
        if (form == null) return 0;
        if (form.contains("~")) {
            String[] values = form.split("~");
            int min = Math.max(Integer.parseInt(values[0]), 0);
            int max = Math.max(Integer.parseInt(values[1]), 0);
            if (min > max) {
                int temp = min;
                min = max;
                max = temp;
            }
            return Math.max(new Random().nextInt(max - min) + min, 0);
        }
        return Math.max(0, Integer.parseInt(form));
    }

    /**
     * @param item the tool used to break the block
     * @return the exp drop of the block with the tool
     */
    public int getExpDrop(ItemStack item) {
        if ((!isValidTool(item) && preperties.isLootNeedTool()) ||
            (item.getEnchantmentLevel(Enchantment.SILK_TOUCH) > 0 && preperties.isSilkTouchable()))
            return 0;
        return getExpDrop();
    }

    /**
     * @return the custom block properties
     */
    public CustomBlockProperties getPreperties() {
        return preperties;
    }

    /**
     * @return persistent data of the block
     */
    public Map<String, Object> getData() {
        ConfigurationSection dataSection = section.getConfigurationSection("data");
        if (dataSection == null) return null;
        return dataSection.getValues(true);
    }

    /**
     * @return block data for particles when breaking the block
     */
    public BlockData getBreakParticleData() {
        String data = section.getString("effects.break-particle");
        if (data == null) return Material.STONE.createBlockData();
        Material mat = Material.getMaterial(data);
        if (mat == null) return Material.STONE.createBlockData();
        return mat.createBlockData();
    }

    /**
     * @return sound when breaking the block
     */
    public Sound getBreakSound() {
        String sound = section.getString("effects.break-sound");
        if (sound == null) return Sound.BLOCK_STONE_BREAK;
        try {
            return Sound.valueOf(sound);
        } catch (IllegalArgumentException e) {
            return Sound.BLOCK_STONE_BREAK;
        }
    }
}
