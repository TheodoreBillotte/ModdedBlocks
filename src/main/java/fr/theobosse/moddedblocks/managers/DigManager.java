package fr.theobosse.moddedblocks.managers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import fr.theobosse.moddedblocks.api.blocks.CustomBlock;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Hashtable;
import java.util.Random;

public class DigManager {

    private static final Hashtable<OfflinePlayer, CustomBlockInfo> diggers = new Hashtable<>();

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
        private final boolean isValidTool;
        private double breakTime;
        private final double initialBreakTime;
        private int prevState = -1;

        public CustomBlockInfo(Player player, Block block, CustomBlock customBlock) {
            this.block = block;
            this.customBlock = customBlock;
            this.tool = player.getInventory().getItemInMainHand();
            this.breakTime = customBlock.getData().getBreakTime(tool);
            this.isValidTool = customBlock.getData().isValidTool(tool, false);
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
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.BLOCK_BREAK_ANIMATION);
            packet.getIntegers().write(0, animId);
            packet.getBlockPositionModifier().write(0, new BlockPosition(block.getLocation().toVector()));
            packet.getIntegers().write(1, animState);
            for (Player p : player.getWorld().getPlayers())
                ProtocolLibrary.getProtocolManager().sendServerPacket(p, packet);
            prevState = animState;
        }

        public double getSpeedModifier() {
            double speedModifier = 1;
            if (miningFatigue != null)
                speedModifier *= Math.pow(0.3, Math.max(Math.min(miningFatigue.getAmplifier() + 1, 4), 0));
            PotionEffect haste = player.getPotionEffect(PotionEffectType.FAST_DIGGING);
            if (haste != null)
                speedModifier *= 0.2 * (haste.getAmplifier() + 1) + 1;
            if (player.isUnderWater() && !hasAquaAffinity())
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
            switch (item.getType()) {
                case DIAMOND_PICKAXE:
                case GOLDEN_PICKAXE:
                case IRON_PICKAXE:
                case STONE_PICKAXE:
                case WOODEN_PICKAXE:
                    return PICKAXE;
                case DIAMOND_AXE:
                case GOLDEN_AXE:
                case IRON_AXE:
                case STONE_AXE:
                case WOODEN_AXE:
                    return AXE;
                case DIAMOND_SHOVEL:
                case GOLDEN_SHOVEL:
                case IRON_SHOVEL:
                case STONE_SHOVEL:
                case WOODEN_SHOVEL:
                    return SHOVEL;
                case DIAMOND_HOE:
                case GOLDEN_HOE:
                case IRON_HOE:
                case STONE_HOE:
                case WOODEN_HOE:
                    return HOE;
                case DIAMOND_SWORD:
                case GOLDEN_SWORD:
                case IRON_SWORD:
                case STONE_SWORD:
                case WOODEN_SWORD:
                    return SWORD;
                case SHEARS:
                    return SHEARS;
                default:
                    return OTHER;
            }
        }

        public static ToolType getToolType(String type) {
            if (type == null) return OTHER;
            switch (type) {
                case "PICKAXE":
                    return PICKAXE;
                case "AXE":
                    return AXE;
                case "SHOVEL":
                    return SHOVEL;
                case "HOE":
                    return HOE;
                case "SWORD":
                    return SWORD;
                case "SHEARS":
                    return SHEARS;
                default:
                    return OTHER;
            }
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
            switch (item.getType()) {
                case NETHERITE_PICKAXE:
                case NETHERITE_AXE:
                case NETHERITE_SHOVEL:
                case NETHERITE_HOE:
                case NETHERITE_SWORD:
                    return NETHERITE;
                case DIAMOND_PICKAXE:
                case DIAMOND_AXE:
                case DIAMOND_SHOVEL:
                case DIAMOND_HOE:
                case DIAMOND_SWORD:
                    return DIAMOND;
                case GOLDEN_PICKAXE:
                case GOLDEN_AXE:
                case GOLDEN_SHOVEL:
                case GOLDEN_HOE:
                case GOLDEN_SWORD:
                    return GOLD;
                case IRON_PICKAXE:
                case IRON_AXE:
                case IRON_SHOVEL:
                case IRON_HOE:
                case IRON_SWORD:
                    return IRON;
                case STONE_PICKAXE:
                case STONE_AXE:
                case STONE_SHOVEL:
                case STONE_HOE:
                case STONE_SWORD:
                    return STONE;
                case WOODEN_PICKAXE:
                case WOODEN_AXE:
                case WOODEN_SHOVEL:
                case WOODEN_HOE:
                case WOODEN_SWORD:
                    return WOOD;
                default:
                    return OTHER;
            }
        }
    }
}
