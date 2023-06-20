package fr.theobosse.moddedblocks.api.blocks;

import fr.theobosse.moddedblocks.managers.DigManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.*;

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

    public ConfigurationSection getSection() {
        return section;
    }

    public double getBlastResistance() {
        return blastResistance;
    }

    public double getHardness() {
        return hardness;
    }

    public int getToolLevel() {
        return toolLevel;
    }

    public String getTooltype() {
        return tooltype;
    }

    public List<String> getLore() {
        return lore;
    }

    public String getName() {
        return name;
    }

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

    public boolean isValidTool(ItemStack tool, boolean checkMaterial) {
        if (!section.contains("tools.type")) return true;
        String type = section.getString("tools.type");
        DigManager.ToolType toolType = DigManager.ToolType.getToolType(tool);
        DigManager.ToolType blockType = DigManager.ToolType.getToolType(type);
        if (!checkMaterial) return toolType == blockType;
        DigManager.ToolMaterial toolMaterial = DigManager.ToolMaterial.getToolMaterial(tool);
        return toolMaterial.getLevel() >= toolLevel && toolType == blockType;
    }

    public boolean isValidTool(ItemStack tool) {
        return isValidTool(tool, true);
    }

    public List<ItemStack> getDrops(int rolls) {
        Random random = new Random();
        ConfigurationSection lootSection = section.getConfigurationSection("loots.items");
        List<ItemStack> items = new ArrayList<>();

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

            Material material = Material.getMaterial(key);
            for (int i = 0; i < amount * rolls; i++)
                if (random.nextInt(100) < chance && material != null)
                    items.add(new ItemStack(material));
        }
        return items;
    }

    public List<ItemStack> getDrops() {
        return getDrops(1);
    }

    public List<ItemStack> getDrops(ItemStack tool) {
        boolean validTool = isValidTool(tool);
        if (!validTool && preperties.isLootNeedTool()) return new ArrayList<>();
        if (!validTool) return getDrops();
        if (preperties.isSilkTouchable() && tool.containsEnchantment(org.bukkit.enchantments.Enchantment.SILK_TOUCH))
            return Collections.singletonList(customBlock.getItem());
        if (!preperties.isFortunate()) return getDrops();
        int rolls = 1;
        int looting = tool.getEnchantmentLevel(org.bukkit.enchantments.Enchantment.LOOT_BONUS_BLOCKS);
        Random random = new Random();
        for (int i = 0; i < looting; i++)
            if (random.nextInt(100) <= 33) rolls++;
        return getDrops(rolls);
    }

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

    public int getExpDrop(ItemStack item) {
        if ((!isValidTool(item) && preperties.isLootNeedTool()) ||
            (item.getEnchantmentLevel(Enchantment.SILK_TOUCH) > 0 && preperties.isSilkTouchable()))
            return 0;
        return getExpDrop();
    }

    public CustomBlockProperties getPreperties() {
        return preperties;
    }

    public Map<String, Object> getData() {
        ConfigurationSection dataSection = section.getConfigurationSection("data");
        if (dataSection == null) return null;
        return dataSection.getValues(true);
    }

    public BlockData getBreakParticleData() {
        String data = section.getString("effects.break-particle");
        if (data == null) return Material.STONE.createBlockData();
        Material mat = Material.getMaterial(data);
        if (mat == null) return Material.STONE.createBlockData();
        return mat.createBlockData();
    }

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
