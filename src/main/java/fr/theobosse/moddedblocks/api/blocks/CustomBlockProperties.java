package fr.theobosse.moddedblocks.api.blocks;

import org.bukkit.configuration.ConfigurationSection;

/**
 * A class to manage custom blocks properties.
 */
public class CustomBlockProperties {

    private final ConfigurationSection section;

    public CustomBlockProperties(ConfigurationSection section) {
        this.section = section;
    }

    public ConfigurationSection getSection() {
        return section;
    }

    /**
     * @return if the custom block is breakable
     */
    public boolean isBreakable() {
        if (section == null || !section.contains("breakable")) return true;
        return section.getBoolean("breakable");
    }

    /**
     * @return if the custom block is pushable
     */
    public boolean isPushable() {
        if (section == null || !section.contains("pushable")) return true;
        return section.getBoolean("pushable");
    }

    /**
     * @return if the custom block has gravity
     */
    public boolean hasGravity() {
        if (section == null || !section.contains("gravity")) return false;
        return section.getBoolean("gravity");
    }

    /**
     * @return if the custom block is explosive
     */
    public boolean isExplosive() {
        if (section == null || !section.contains("explosive")) return false;
        return section.getBoolean("explosive");
    }

    /**
     * @return if the custom block is flammable
     */
    public boolean isFlammable() {
        if (section == null || !section.contains("flammable")) return false;
        return section.getBoolean("flammable");
    }

    /**
     * @return if the custom block is affected by fortune
     */
    public boolean isFortunate() {
        if (section == null || !section.contains("fortunate")) return false;
        return section.getBoolean("fortunate");
    }

    /**
     * @return if the custom block is affected by silk touch
     */
    public boolean isSilkTouchable() {
        if (section == null || !section.contains("silk-touchable")) return true;
        return section.getBoolean("silk-touchable");
    }

    /**
     * @return true if the block need the correct material to be mined
     */
    public boolean isLootNeedTool() {
        if (section == null || !section.contains("loot-need-tool")) return false;
        return section.getBoolean("loot-need-tool");
    }

}
