package studio.magemonkey.fabled.dynamic.mechanic;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.potion.PotionTypes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEffect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Fabled © 2024
 * studio.magemonkey.fabled.dynamic.mechanic.GlowMechanic
 */
public class GlowMechanic extends MechanicComponent {
    private static final String DURATION = "duration";

    @Override
    public String getKey() {
        return "glow";
    }

    @Override
    public boolean execute(final LivingEntity caster,
                           final int level,
                           final List<LivingEntity> targets,
                           boolean force) {
        if (caster instanceof Player player) {
            var duration = (int) parseValues(caster, DURATION, level, 5) * 20;
            for (var target : targets) {
                var packet = new WrapperPlayServerEntityEffect(target.getEntityId(), PotionTypes.GLOWING, 1, duration, (byte) 0);
                PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
            }
            return true;
        }
        return false;
    }
}
