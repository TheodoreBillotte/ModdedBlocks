package fr.theobosse.moddedblocks.managers;

import net.minecraft.network.protocol.Packet;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketManager {

    private final Player player;
    private final CraftPlayer craftPlayer;

    public PacketManager(Player player) {
        this.player = player;
        this.craftPlayer = (CraftPlayer) player;
    }

    public Player getPlayer() {
        return player;
    }

    public CraftPlayer getCraftPlayer() {
        return craftPlayer;
    }

    public void sendPacket(Packet<?> packet) {
        craftPlayer.getHandle().b.a(packet);
    }

}
