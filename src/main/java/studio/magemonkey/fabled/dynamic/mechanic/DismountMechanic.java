package studio.magemonkey.fabled.dynamic.mechanic;

import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * Fabled © 2024
 * studio.magemonkey.fabled.dynamic.mechanic.GlowMechanic
 */
public class DismountMechanic extends MechanicComponent {
    @Override
    public String getKey() {
        return "dismount";
    }

    @Override
    public boolean execute(final LivingEntity caster,
                           final int level,
                           final List<LivingEntity> targets,
                           boolean force) {
        return targets.getFirst().removePassenger(caster);
    }
}
