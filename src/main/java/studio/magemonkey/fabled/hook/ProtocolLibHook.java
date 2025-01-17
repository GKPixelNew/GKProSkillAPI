package studio.magemonkey.fabled.hook;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import studio.magemonkey.fabled.Fabled;

public class ProtocolLibHook {
    private final Fabled          plugin;
    @Getter
    private final ProtocolManager protocolManager;

    public ProtocolLibHook(Fabled plugin) {
        this.plugin = plugin;
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    public void register(PacketAdapter listener) {
        protocolManager.addPacketListener(listener);
    }

    public void unregister() {protocolManager.removePacketListeners(plugin);}

    public void unregister(PacketAdapter listener) {protocolManager.removePacketListener(listener);}

    public void unregister(Iterable<PacketAdapter> listeners) {
        listeners.forEach(c -> unregister(c));
    }

    @SneakyThrows
    public void broadcastToNearby(Player player, PacketContainer packet) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (player.equals(p) || !p.getWorld().equals(player.getWorld())) continue;

            double dist = player.getLocation().distanceSquared(p.getLocation());
            if (dist < Bukkit.getViewDistance() * 16) {
                protocolManager.sendServerPacket(p, packet);
            }
        }
    }
}
