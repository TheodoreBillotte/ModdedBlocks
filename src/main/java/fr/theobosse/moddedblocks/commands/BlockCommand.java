package fr.theobosse.moddedblocks.commands;

import fr.theobosse.moddedblocks.api.blocks.CustomBlock;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BlockCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) return false;
        Player player = (Player) commandSender;

        Material material = Material.STONE;
        int model = 1;
        if (strings.length > 0) {
            try {
                model = Integer.parseInt(strings[0]);
            } catch (IllegalArgumentException e) {
                player.sendPlainMessage("§cInvalid number");
                return false;
            }
        }
        player.getInventory().addItem(CustomBlock.getCustomBlock(model).getItem());
        return true;
    }

}
