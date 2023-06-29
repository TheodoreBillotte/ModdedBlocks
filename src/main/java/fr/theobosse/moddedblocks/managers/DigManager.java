package fr.theobosse.moddedblocks.managers;

import fr.theobosse.moddedblocks.api.blocks.CustomBlock;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimation;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Random;

public class DigManager {

    private static final HashMap<OfflinePlayer, CustomBlockInfo> diggers = new HashMap<>();

    public static void addDigger(Player player, Block block, CustomBlock customBlock) {
        CustomBlockInfo info = new CustomBlockInfo(player, block, customBlock);
        diggers.put(player, info);
    }

    public static void addDigger(Player player, CustomBlockInfo info) {
        diggers.put(player, info);
    }

    public static void removeDigger(Player player) {
        diggers.remove(player);
    }

    public static CustomBlockInfo getDigger(Player player) {
        return diggers.get(player);
    }

    public static class CustomBlockInfo {

        private final Block block;
        private final CustomBlock customBlock;
        private final PotionEffect miningFatigue;
        private final Player player;
        private final int animId;
        private final ItemStack tool;
        private double breakTime;
        private final double initialBreakTime;
        private int prevState = -1;

        public CustomBlockInfo(Player player, Block block, CustomBlock customBlock) {
            this.block = block;
            this.customBlock = customBlock;
            this.tool = player.getInventory().getItemInMainHand();
            this.breakTime = customBlock.getData().getBreakTime(tool);
            this.initialBreakTime = breakTime;
            this.miningFatigue = player.getPotionEffect(PotionEffectType.SLOW_DIGGING);
            this.player = player;
            this.animId = new Random().nextInt(Integer.MAX_VALUE);
        }

        public CustomBlock getCustomBlock() {
            return customBlock;
        }

        public Block getBlock() {
            return block;
        }

        public double getBreakTime() {
            return breakTime;
        }

        public double getInitialBreakTime() {
            return initialBreakTime;
        }

        public ItemStack getTool() {
            return tool;
        }

        public void setBreakTime(double breakTime) {
            this.breakTime = breakTime;
        }

        public void addBreakTime(double time) {
            this.breakTime += time;
        }

        public void removeBreakTime(double time) {
            this.breakTime -= time;
        }

        public PotionEffect getMiningFatigue() {
            return miningFatigue;
        }

        public Player getPlayer() {
            return player;
        }

        public int getPrevState() {
            return prevState;
        }

        public int getAnimId() {
            return animId;
        }

        public void playBreakAnimation() {
            int animState = 10 - ((int) Math.floor((breakTime / initialBreakTime) * 10) + 1);
            playBreakAnimation(animState);
        }

        public void playBreakAnimation(int animState) {
            if (animState == prevState) return;
            PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(animId, new BlockPosition(block.getX(), block.getY(), block.getZ()), animState);
            for (Player p : player.getWorld().getPlayers())
                PacketManager.sendPacket(p, packet);
            prevState = animState;
        }

        public double getSpeedModifier() {
            double speedModifier = 1;
            if (miningFatigue != null)
                speedModifier *= Math.pow(0.3, Math.max(Math.min(miningFatigue.getAmplifier() + 1, 4), 0));
            PotionEffect haste = player.getPotionEffect(PotionEffectType.FAST_DIGGING);
            if (haste != null)
                speedModifier *= 0.2 * (haste.getAmplifier() + 1) + 1;
            if (player.getLocation().clone().add(0, 1, 0).getBlock().getType() == Material.WATER && !hasAquaAffinity())
                speedModifier /= 5;
            if (!player.isOnGround())
                speedModifier /= 5;
            return speedModifier;
        }

        private boolean hasAquaAffinity() {
            ItemStack helmet = player.getInventory().getHelmet();
            return helmet != null && helmet.getEnchantmentLevel(Enchantment.WATER_WORKER) > 0;
        }
    }

    public enum ToolType {
        PICKAXE,
        AXE,
        SHOVEL,
        HOE,
        SWORD,
        SHEARS,
        OTHER;

        public static ToolType getToolType(ItemStack item) {
            if (item == null) return OTHER;
            return switch (item.getType()) {
                case DIAMOND_PICKAXE, GOLDEN_PICKAXE, IRON_PICKAXE, STONE_PICKAXE, WOODEN_PICKAXE -> PICKAXE;
                case DIAMOND_AXE, GOLDEN_AXE, IRON_AXE, STONE_AXE, WOODEN_AXE -> AXE;
                case DIAMOND_SHOVEL, GOLDEN_SHOVEL, IRON_SHOVEL, STONE_SHOVEL, WOODEN_SHOVEL -> SHOVEL;
                case DIAMOND_HOE, GOLDEN_HOE, IRON_HOE, STONE_HOE, WOODEN_HOE -> HOE;
                case DIAMOND_SWORD, GOLDEN_SWORD, IRON_SWORD, STONE_SWORD, WOODEN_SWORD -> SWORD;
                case SHEARS -> SHEARS;
                default -> OTHER;
            };
        }

        public static ToolType getToolType(String type) {
            if (type == null) return OTHER;
            return switch (type) {
                case "PICKAXE" -> PICKAXE;
                case "AXE" -> AXE;
                case "SHOVEL" -> SHOVEL;
                case "HOE" -> HOE;
                case "SWORD" -> SWORD;
                case "SHEARS" -> SHEARS;
                default -> OTHER;
            };
        }

        public static boolean isTool(ItemStack item) {
            return getToolType(item) != OTHER;
        }
    }

    public enum ToolMaterial {
        WOOD(2, 0),
        STONE(4, 1),
        IRON(6, 2),
        GOLD(12, 0),
        DIAMOND(8, 3),
        NETHERITE(9, 4),
        OTHER(1, 0);

        private final int speed;
        private final int level;

        ToolMaterial(int speed, int level) {
            this.speed = speed;
            this.level = level;
        }

        public int getSpeed() {
            return speed;
        }

        public int getLevel() {
            return level;
        }

        public static ToolMaterial getToolMaterial(ItemStack item) {
            if (item == null) return OTHER;
            return switch (item.getType()) {
                case NETHERITE_PICKAXE, NETHERITE_AXE, NETHERITE_SHOVEL, NETHERITE_HOE, NETHERITE_SWORD -> NETHERITE;
                case DIAMOND_PICKAXE, DIAMOND_AXE, DIAMOND_SHOVEL, DIAMOND_HOE, DIAMOND_SWORD -> DIAMOND;
                case GOLDEN_PICKAXE, GOLDEN_AXE, GOLDEN_SHOVEL, GOLDEN_HOE, GOLDEN_SWORD -> GOLD;
                case IRON_PICKAXE, IRON_AXE, IRON_SHOVEL, IRON_HOE, IRON_SWORD -> IRON;
                case STONE_PICKAXE, STONE_AXE, STONE_SHOVEL, STONE_HOE, STONE_SWORD -> STONE;
                case WOODEN_PICKAXE, WOODEN_AXE, WOODEN_SHOVEL, WOODEN_HOE, WOODEN_SWORD -> WOOD;
                default -> OTHER;
            };
        }
    }
}
