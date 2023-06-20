package fr.theobosse.moddedblocks.api.blocks;

import fr.theobosse.moddedblocks.tools.Configs;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.Map;

public class BlockPersistentData {

    private YamlConfiguration config;
    private String blockId;

    private final PersistentDataType<?, ?>[] types = {
            PersistentDataType.BOOLEAN,
            PersistentDataType.BYTE,
            PersistentDataType.BYTE_ARRAY,
            PersistentDataType.DOUBLE,
            PersistentDataType.FLOAT,
            PersistentDataType.INTEGER,
            PersistentDataType.INTEGER_ARRAY,
            PersistentDataType.LONG,
            PersistentDataType.LONG_ARRAY,
            PersistentDataType.SHORT,
            PersistentDataType.STRING,
            PersistentDataType.TAG_CONTAINER,
            PersistentDataType.TAG_CONTAINER_ARRAY
    };

    public BlockPersistentData(Block block) {
        this.blockId = getBlockId(block);
        this.config = Configs.getConfig("blocks-data");
    }

    private String getBlockId(Block block) {
        return block.getWorld().getName() + "," + block.getX() + "," + block.getY() + "," + block.getZ();
    }

    public void set(String fieldName, Object value) {
        config.set(blockId + "." + fieldName, value);
        Configs.save("blocks-data");
    }

    public void setValues(Map<String, Object> values) {
        values.forEach((key, value) -> {
            config.set(blockId + "." + key, value);
        });
        Configs.save("blocks-data");
    }

    public void addPersistentDataContainer(PersistentDataContainer container) {
        container.getKeys().forEach(key -> {
            for (PersistentDataType<?, ?> type : types) {
                if (container.has(key, type)) {
                    config.set(blockId + "." + key.getKey(), container.get(key, type));
                    break;
                }
            }
        });
        Configs.save("blocks-data");
    }

    public Object get(String fieldName) {
        return config.get(blockId + "." + fieldName);
    }

    public <T> T get(String fieldName, Class<T> type) {
        return config.getObject(blockId + "." + fieldName, type);
    }

    public Vector getVector(String fieldName) {
        return config.getVector(blockId + "." + fieldName);
    }

    public OfflinePlayer getPlayer(String fieldName) {
        return config.getOfflinePlayer(blockId + "." + fieldName);
    }

    public ItemStack getItemStack(String fieldName) {
        return config.getItemStack(blockId + "." + fieldName);
    }

    public Location getLocation(String fieldName) {
        return config.getLocation(blockId + "." + fieldName);
    }

    public Color getColor(String fieldName) {
        return config.getColor(blockId + "." + fieldName);
    }

    public Map<String, Object> getValues(String fieldName) {
        if (!contains(fieldName)) return null;
        return config.getConfigurationSection(blockId + "." + fieldName).getValues(false);
    }

    public Map<String, Object> getValues() {
        if (!this.config.contains(blockId)) return null;
        return config.getConfigurationSection(blockId).getValues(true);
    }

    public boolean contains(String fieldName) {
        return config.contains(blockId + "." + fieldName);
    }

    public void remove(String fieldName) {
        if (!contains(fieldName)) return;
        config.set(blockId + "." + fieldName, null);
        Configs.save("blocks-data");
    }

    public void clear() {
        config.set(blockId, null);
        Configs.save("blocks-data");
    }

    public void copyFrom(BlockPersistentData data) {
        Map<String, Object> values = data.getValues();
        if (values == null) return;

        values.forEach((key, value) -> config.set(blockId + "." + key, value));
        Configs.save("blocks-data");
    }

    public String getBlockId() {
        return blockId;
    }

    public YamlConfiguration getConfig() {
        return config;
    }
}
