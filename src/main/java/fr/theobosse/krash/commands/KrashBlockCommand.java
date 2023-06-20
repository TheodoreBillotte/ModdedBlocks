package fr.theobosse.krash.commands;

import fr.theobosse.krash.ModdedBlocks;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class KrashBlockCommand implements CommandExecutor {

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
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(1000 + model);
        PersistentDataContainer container = meta.getPersistentDataContainer();
        meta.setDisplayName("§6§lKrash Block");
        container.set(new NamespacedKey(ModdedBlocks.getInstance(), "text"), PersistentDataType.STRING, "HELLO");
        item.setItemMeta(meta);
        player.getInventory().addItem(item);
        return true;
    }

}
