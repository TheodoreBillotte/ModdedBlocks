package fr.theobosse.moddedblocks.api.blocks;

import org.bukkit.inventory.ItemStack;

import java.security.PublicKey;

public interface CustomBlockItemRegister {

    /**
     * Register a custom block drop item
     * @param itemId the item id
     * @return the item stack
     */
    ItemStack getItemStack(String itemId);

}
