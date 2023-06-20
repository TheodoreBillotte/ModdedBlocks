package fr.theobosse.krash.events;

import com.destroystokyo.paper.ParticleBuilder;
import fr.theobosse.krash.ModdedBlocks;
import fr.theobosse.krash.api.blocks.BlockPersistentData;
import fr.theobosse.krash.api.events.ModdedBlockDestroyedEvent;
import fr.theobosse.krash.managers.DigManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

public class CustomBlockEvents implements Listener {

    private final Hashtable<OfflinePlayer, DigManager.CustomBlockInfo> tempDiggers = new Hashtable<>();


    @EventHandler
    public void onCustomBlockPlaced(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        ItemStack item = event.getItemInHand();
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasCustomModelData()) return;
        int configId = meta.getCustomModelData() - 1000;
        if (!item.getType().equals(Material.STONE) || configId < 0 || configId > 159) return;
        fr.theobosse.krash.api.blocks.CustomBlock customBlock = fr.theobosse.krash.api.blocks.CustomBlock.getCustomBlock(configId);
        if (customBlock == null) return;
        MultipleFacing blockData = customBlock.getBlockData();
        block.setType(blockData.getMaterial(), false);
        block.setBlockData(blockData, false);
        Map<String, Object> data = customBlock.getData().getData();
        if (data != null) {
            BlockPersistentData persistentData = new BlockPersistentData(block);
            persistentData.setValues(data);
        }
    }

    @EventHandler
    public void onDamage(BlockDamageEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode().equals(org.bukkit.GameMode.CREATIVE)) return;
        Block block = event.getBlock();
        if (!fr.theobosse.krash.api.blocks.CustomBlock.isMushroomBlock(block)) return;
        fr.theobosse.krash.api.blocks.CustomBlock customBlock = fr.theobosse.krash.api.blocks.CustomBlock.getCustomBlock(block);
        if (customBlock == null) return;
        DigManager.CustomBlockInfo info = tempDiggers.get(player);
        DigManager.CustomBlockInfo info2 = DigManager.getDigger(player);
        if (info == null && info2 == null) {
            DigManager.addDigger(player, block, customBlock);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, Integer.MAX_VALUE, false, false, false));
        }
    }

    @EventHandler
    public void onDamageAbort(BlockDamageAbortEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode().equals(org.bukkit.GameMode.CREATIVE)) return;
        abortMining(player);
    }

    @EventHandler
    public void onBlockBreaking(PlayerAnimationEvent event) {
        Player player = event.getPlayer();
        if (!event.getAnimationType().equals(PlayerAnimationType.ARM_SWING)) return;
        DigManager.CustomBlockInfo tempInfo = tempDiggers.get(player);
        if (tempInfo != null) {
            DigManager.addDigger(player, tempInfo);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, Integer.MAX_VALUE, false, false, false));
            tempDiggers.remove(player);
        }
        DigManager.CustomBlockInfo info = DigManager.getDigger(player);
        if (info == null) return;
        if (!info.getCustomBlock().getData().getPreperties().isBreakable()) return;
        info.removeBreakTime(info.getSpeedModifier());
        info.playBreakAnimation();
        if (info.getBreakTime() <= 0) {
            ModdedBlockDestroyedEvent destroyEvent = new ModdedBlockDestroyedEvent(info.getBlock());
            Bukkit.getPluginManager().callEvent(destroyEvent);
            if (destroyEvent.isCancelled()) return;
            BlockPersistentData persistent = new BlockPersistentData(info.getBlock());
            if (persistent.getValues() == null) return;
            persistent.clear();

            info.getBlock().setType(Material.AIR, false);
            abortMining(player);
            Location dropLoc = info.getBlock().getLocation().add(0.5, 0.5, 0.5);
            ItemStack item = player.getInventory().getItemInMainHand();
            BlockData data = info.getCustomBlock().getData().getBreakParticleData();
            new ParticleBuilder(Particle.BLOCK_CRACK).
                    allPlayers().count(50).offset(0.4, 0.4, 0.4).
                    extra(0.5).location(dropLoc).data(data)
                    .spawn();
            player.getWorld().playSound(info.getBlock().getLocation(), info.getCustomBlock().getData().getBreakSound(), 5, 1);
            if (!info.getCustomBlock().getData().isValidTool(item)) return;
            for (ItemStack drop : info.getCustomBlock().getData().getDrops(item))
                info.getBlock().getWorld().dropItemNaturally(dropLoc, drop);
            int exp = info.getCustomBlock().getData().getExpDrop(item);
            if (exp > 0)
                info.getBlock().getWorld().spawn(dropLoc, ExperienceOrb.class).setExperience(exp);
            ItemMeta meta = item.getItemMeta();
            if (meta instanceof Damageable) {
                Damageable damageable = (Damageable) meta;
                Random random = new Random();
                if (random.nextInt(100) < 100 / (meta.getEnchantLevel(Enchantment.DURABILITY) + 1)) {
                    damageable.setDamage(damageable.getDamage() + 1);
                    item.setItemMeta(meta);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMushroomPlaced(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        if (!fr.theobosse.krash.api.blocks.CustomBlock.isMushroomBlock(block) || fr.theobosse.krash.api.blocks.CustomBlock.isCustomBlock(block)) return;
        block.setType(block.getType(), false);
    }

    @EventHandler(ignoreCancelled = true)
    public void onMushroomUpdate(BlockPhysicsEvent event) {
        if (fr.theobosse.krash.api.blocks.CustomBlock.isMushroomBlock(event.getBlock())) {
            event.setCancelled(true);
            event.getBlock().getState().update(true, false);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (player.getGameMode().equals(org.bukkit.GameMode.CREATIVE) || !fr.theobosse.krash.api.blocks.CustomBlock.isCustomBlock(block)) return;
        DigManager.CustomBlockInfo info = DigManager.getDigger(player);
        if (info != null) {
            if (info.getBreakTime() == info.getInitialBreakTime()) {
                DigManager.removeDigger(player);
                tempDiggers.put(player, info);
                player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 5, Integer.MAX_VALUE, false, false, false));
                Bukkit.getScheduler().runTaskLater(ModdedBlocks.getInstance(), () -> {
                    tempDiggers.remove(player);
                }, 5);
            }
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        DigManager.CustomBlockInfo info = DigManager.getDigger(player);
        if (info == null) return;
        abortMining(player);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> customBlockExplode(block, event.getLocation()));
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(block -> customBlockExplode(block, event.getBlock().getLocation()));
    }

    private boolean customBlockExplode(Block block, Location loc) {
        fr.theobosse.krash.api.blocks.CustomBlock customBlock = fr.theobosse.krash.api.blocks.CustomBlock.getCustomBlock(block);
        if (customBlock == null || loc.distance(block.getLocation()) > 10 - customBlock.getData().getBlastResistance())
            return false;
        block.setType(Material.AIR, false);
        customBlock.getData().getDrops().forEach(drop -> block.getWorld().dropItemNaturally(block.getLocation(), drop));
        return true;
    }

    private void abortMining(Player player) {
        DigManager.CustomBlockInfo info = DigManager.getDigger(player);
        if (info == null) return;
        info.playBreakAnimation(10);
        player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
        if (info.getMiningFatigue() != null)
            player.addPotionEffect(info.getMiningFatigue());
        DigManager.removeDigger(player);
    }

}
