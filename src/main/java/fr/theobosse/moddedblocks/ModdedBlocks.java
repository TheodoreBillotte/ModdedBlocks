package fr.theobosse.moddedblocks;

import fr.theobosse.moddedblocks.api.blocks.CustomBlock;
import fr.theobosse.moddedblocks.commands.BlockCommand;
import fr.theobosse.moddedblocks.events.*;
import fr.theobosse.moddedblocks.tools.Configs;
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

        Objects.requireNonNull(getCommand("block")).setExecutor(new BlockCommand());

        CustomBlock.loadCustomBlocks();
    }

    @Override
    public void onDisable() {
        getServer().getLogger().info("KrashAPI is now disabled");
    }

    public static ModdedBlocks getInstance() {
        return instance;
    }
}
