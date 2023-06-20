package fr.theobosse.krash.api.blocks;

import org.bukkit.configuration.ConfigurationSection;

public class CustomBlockProperties {

    private final ConfigurationSection section;

    public CustomBlockProperties(ConfigurationSection section) {
        this.section = section;
    }

    public ConfigurationSection getSection() {
        return section;
    }

    public boolean isBreakable() {
        if (section == null || !section.contains("breakable")) return true;
        return section.getBoolean("breakable");
    }

    public boolean isPushable() {
        if (section == null || !section.contains("pushable")) return true;
        return section.getBoolean("pushable");
    }

    public boolean hasGravity() {
        if (section == null || !section.contains("gravity")) return false;
        return section.getBoolean("gravity");
    }

    public boolean isExplosive() {
        if (section == null || !section.contains("explosive")) return false;
        return section.getBoolean("explosive");
    }

    public boolean isFlammable() {
        if (section == null || !section.contains("flammable")) return false;
        return section.getBoolean("flammable");
    }

    public boolean isFortunate() {
        if (section == null || !section.contains("fortunate")) return false;
        return section.getBoolean("fortunate");
    }

    public boolean isSilkTouchable() {
        if (section == null || !section.contains("silk-touchable")) return true;
        return section.getBoolean("silk-touchable");
    }

    public boolean isLootNeedTool() {
        if (section == null || !section.contains("loot-need-tool")) return false;
        return section.getBoolean("loot-need-tool");
    }

}
