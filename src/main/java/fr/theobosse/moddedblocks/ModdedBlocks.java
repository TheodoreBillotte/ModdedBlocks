package fr.theobosse.moddedblocks;

import fr.theobosse.moddedblocks.api.blocks.CustomBlock;
import fr.theobosse.moddedblocks.commands.BlockCommand;
import fr.theobosse.moddedblocks.events.*;
import fr.theobosse.moddedblocks.managers.PacketManager;
import fr.theobosse.moddedblocks.tools.Configs;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class ModdedBlocks extends JavaPlugin {

    private static ModdedBlocks instance;

    @Override
    public void onEnable() {
        instance = this;
        PluginManager pm = getServer().getPluginManager();
        Configs.register("blocks-data");
        Configs.register("custom-blocks");
        Configs.load();

        pm.registerEvents(new BlockPlaced(), this);
        pm.registerEvents(new BlockBreaked(), this);
        pm.registerEvents(new BlockMoved(), this);
        pm.registerEvents(new BlockFall(), this);
        pm.registerEvents(new CustomBlockEvents(), this);
        pm.registerEvents(new GeneratorEvents(), this);
        pm.registerEvents(new PacketManager.ConnectionEvents(), this);

        Objects.requireNonNull(getCommand("block")).setExecutor(new BlockCommand());

        CustomBlock.loadCustomBlocks();
        CustomBlock.registerItem("custom_block", itemId -> {
            int model;
            try {
                model = Integer.parseInt(itemId);
            } catch (NumberFormatException e) {
                return null;
            }
            CustomBlock data = CustomBlock.getCustomBlock(model);
            if (data == null) return null;
            return data.asItemStack();
        });
    }

    public static ModdedBlocks getInstance() {
        return instance;
    }
}
