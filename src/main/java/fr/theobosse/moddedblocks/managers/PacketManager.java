package fr.theobosse.moddedblocks.managers;

import fr.theobosse.moddedblocks.ModdedBlocks;
import net.minecraft.network.protocol.Packet;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public class PacketManager {

    private final Player player;
    private final Object playerConnection;
    private final Method sendPacketMethod;
    private final String nmsVersion;

    public PacketManager(Player player) {
        this.player = player;
        this.playerConnection = getPlayerConnection();
        this.sendPacketMethod = getSendPacketMethod();
        this.nmsVersion = getNMSVersion();
    }

    private String getNMSVersion(){
        String v = ModdedBlocks.getInstance().getServer().getClass().getPackage().getName();
        return v.substring(v.lastIndexOf('.') + 1);
    }

    private Object getPlayerConnection() {
        try {
            Object handleClass = player.getClass().getMethod("getHandle").invoke(player);
            return handleClass.getClass().getField("playerConnection").get(handleClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Method getSendPacketMethod() {
        try {
            return playerConnection.getClass().getMethod("sendPacket");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Player getPlayer() {
        return player;
    }

    public String getNmsVersion() {
        return nmsVersion;
    }

    public void sendPacket(Packet<?> packet) {
        if (sendPacketMethod == null || playerConnection == null) return;
        try {
            sendPacketMethod.invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
