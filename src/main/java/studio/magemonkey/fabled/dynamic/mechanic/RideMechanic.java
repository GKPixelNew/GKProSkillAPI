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
public class RideMechanic extends MechanicComponent {
    @Override
    public String getKey() {
        return "ride";
    }

    @Override
    public boolean execute(final LivingEntity caster,
                           final int level,
                           final List<LivingEntity> targets,
                           boolean force) {
        return targets.getFirst().addPassenger(caster);
    }
}
