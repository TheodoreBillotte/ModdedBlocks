package fr.theobosse.krash;

import fr.theobosse.krash.commands.KrashBlockCommand;
import fr.theobosse.krash.events.*;
import fr.theobosse.krash.tools.Configs;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class ModdedBlocks extends JavaPlugin {

    private static ModdedBlocks instance;

    @Override
    public void onEnable() {
        getServer().getLogger().info("KrashAPI is now enabled");

        instance = this;
        PluginManager pm = getServer().getPluginManager();
        Configs.register("blocks-data");
        Configs.register("custom-blocks");
        Configs.load();

        pm.registerEvents(new BlockListener(), this);
        pm.registerEvents(new BlockPlaced(), this);
        pm.registerEvents(new BlockBreaked(), this);
        pm.registerEvents(new BlockMoved(), this);
        pm.registerEvents(new BlockFall(), this);
        pm.registerEvents(new CustomBlockEvents(), this);

        Objects.requireNonNull(getCommand("block")).setExecutor(new KrashBlockCommand());

        fr.theobosse.krash.api.blocks.CustomBlock.loadCustomBlocks();
    }

    @Override
    public void onDisable() {
        getServer().getLogger().info("KrashAPI is now disabled");
    }

    public static ModdedBlocks getInstance() {
        return instance;
    }
}
