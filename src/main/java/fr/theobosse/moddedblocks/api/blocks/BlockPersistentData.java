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

/**
 * A class to manage persistent data of blocks.
 */
public class BlockPersistentData {

    private final YamlConfiguration config;
    private final String blockId;

    /**
     * The types of persistent data.
     */
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

    /**
     * @param fieldName key name for the value
     * @param value     value as an object
     * @see #set(String, Object) set a value
     */
    public void set(String fieldName, Object value) {
        config.set(blockId + "." + fieldName, value);
        Configs.save("blocks-data");
    }

    /**
     * @param values a map of key names and values
     * @see #setValues(Map) set all values at once
     */
    public void setValues(Map<String, Object> values) {
        values.forEach((key, value) -> config.set(blockId + "." + key, value));
        Configs.save("blocks-data");
    }

    /**
     * @param container a persistent data container to add to the block persistent data
     * @see #addPersistentDataContainer(PersistentDataContainer) add a persistent data container to the block persistent data
     */
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

    /**
     * @param fieldName key name for the value
     * @return the value as an object
     */
    public Object get(String fieldName) {
        return config.get(blockId + "." + fieldName);
    }

    /**
     * @param fieldName key name for the value
     * @param type      the type of the value returned
     * @param <T>       the type of the value returned
     * @return the value as an object of the specified type
     */
    public <T> T get(String fieldName, Class<T> type) {
        return config.getObject(blockId + "." + fieldName, type);
    }

    /**
     * @param fieldName key name for the value
     * @return the value as a Vector
     */
    public Vector getVector(String fieldName) {
        return config.getVector(blockId + "." + fieldName);
    }

    /**
     * @param fieldName key name for the value
     * @return the value as an OfflinePlayer
     */
    public OfflinePlayer getPlayer(String fieldName) {
        return config.getOfflinePlayer(blockId + "." + fieldName);
    }

    /**
     * @param fieldName key name for the value
     * @return the value as an ItemStack
     */
    public ItemStack getItemStack(String fieldName) {
        return config.getItemStack(blockId + "." + fieldName);
    }

    /**
     * @param fieldName key name for the value
     * @return the value as a Location
     */
    public Location getLocation(String fieldName) {
        return config.getLocation(blockId + "." + fieldName);
    }

    /**
     * @param fieldName key name for the value
     * @return the value as a Color
     */
    public Color getColor(String fieldName) {
        return config.getColor(blockId + "." + fieldName);
    }

    /**
     * @param fieldName key name for the value
     * @return the value as a Map
     */
    public Map<String, Object> getValues(String fieldName) {
        if (!contains(fieldName)) return null;
        return config.getConfigurationSection(blockId + "." + fieldName).getValues(false);
    }

    /**
     * @return all the values of the block persistent data
     */
    public Map<String, Object> getValues() {
        if (!this.config.contains(blockId)) return null;
        return config.getConfigurationSection(blockId).getValues(true);
    }

    /**
     * @param fieldName key name for the value
     * @return true if the block persistent data contains the specified key
     */
    public boolean contains(String fieldName) {
        return config.contains(blockId + "." + fieldName);
    }

    /**
     * @param fieldName key name for the value
     * @see #remove(String) remove a value from the block persistent data
     */
    public void remove(String fieldName) {
        if (!contains(fieldName)) return;
        config.set(blockId + "." + fieldName, null);
        Configs.save("blocks-data");
    }

    /**
     * @see #clear() clear all values from the block persistent data
     */
    public void clear() {
        config.set(blockId, null);
        Configs.save("blocks-data");
    }

    /**
     * @param data the block persistent data to copy from
     * @see #copyFrom(BlockPersistentData) copy all values from another block persistent data
     */
    public void copyFrom(BlockPersistentData data) {
        Map<String, Object> values = data.getValues();
        if (values == null) return;

        values.forEach((key, value) -> config.set(blockId + "." + key, value));
        Configs.save("blocks-data");
    }

    /**
     * @return the block id
     */
    public String getBlockId() {
        return blockId;
    }

    /**
     * @return the block persistent data config
     */
    public YamlConfiguration getConfig() {
        return config;
    }
}
