package fr.theobosse.moddedblocks.managers;

import fr.theobosse.moddedblocks.ModdedBlocks;
import fr.theobosse.moddedblocks.tools.Reflection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

public class PacketManager {

    private static final HashMap<OfflinePlayer, PacketManager> packetManagers = new HashMap<>();
    private final Player player;
    private final Object playerConnection;
    private final Reflection.MethodInvoker sendPacketMethod;
    private final String nmsVersion;

    public PacketManager(Player player) {
        this.player = player;
        this.playerConnection = getPlayerConnection();
        this.sendPacketMethod = getSendPacketMethod();
        this.nmsVersion = getNMSVersion();
        packetManagers.put(player, this);
    }

    private String getNMSVersion(){
        String v = ModdedBlocks.getInstance().getServer().getClass().getPackage().getName();
        return v.substring(v.lastIndexOf('.') + 1);
    }

    private Object getPlayerConnection() {
        try {
            Object handleClass = Reflection.getMethod(player.getClass(), "getHandle").invoke(player);
            return Reflection.getField(handleClass.getClass(), PlayerConnection.class, 0).get(handleClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Reflection.MethodInvoker getSendPacketMethod() {
        try {
            return Reflection.getMethodByParameters(playerConnection.getClass(), 0, Packet.class);
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

    public static void sendPacket(Player player, Packet<?> packet) {
        PacketManager packetManager = packetManagers.get(player);
        if (packetManager == null)
            packetManager = new PacketManager(player);
        packetManager.sendPacket(packet);
    }

    public static class ConnectionEvents implements Listener {

        @EventHandler
        public final void onPlayerJoin(PlayerJoinEvent event) {
            new PacketManager(event.getPlayer());
        }

        @EventHandler
        public final void onPlayerQuit(PlayerQuitEvent event) {
            packetManagers.remove(event.getPlayer());
        }

    }

}
