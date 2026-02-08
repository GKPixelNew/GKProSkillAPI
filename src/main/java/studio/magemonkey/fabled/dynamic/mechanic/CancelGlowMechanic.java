package studio.magemonkey.fabled.dynamic.mechanic;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import java.util.List;

public class CancelGlowMechanic extends MechanicComponent {

    @Override
    public String getKey() {
        return "cancel glow";
    }

    @Override
    public boolean execute(final LivingEntity caster,
                           final int level,
                           final List<LivingEntity> targets,
                           boolean force) {

        if (caster instanceof Player player) {
            for (var target : targets) {
                GlowMechanic.stopGlow(player, target);
            }
            return true;
        }
        return false;
    }
}