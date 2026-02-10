package studio.magemonkey.fabled.dynamic.mechanic;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import studio.magemonkey.fabled.hook.BetterModelHook;

import java.util.List;

public class BetterModelAnimateLimbMechanic extends MechanicComponent {
    private static final String LIMB_ID = "limb_id";
    private static final String ANIMATION_ID = "animation_id";
    private static final String LERP_IN = "lerp_in";
    private static final String LERP_OUT = "lerp_out";
    private static final String SPEED = "speed";
    private static final String TYPE = "type";

    @Override
    public String getKey() {
        return "better model animate limb";
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        if (Bukkit.getPluginManager().isPluginEnabled("BetterModel")) {
            for (var target : targets) {
                if (target instanceof Player targetPlayer) {
                    BetterModelHook.animateLimb(targetPlayer,
                            getSettings().getString(LIMB_ID),
                            getSettings().getString(ANIMATION_ID),
                            (int) parseValues(caster, LERP_IN, level, 0.0),
                            (int) parseValues(caster, LERP_OUT, level, 0.0),
                            (float) parseValues(caster, SPEED, level, 1.0),
                            getSettings().getString(TYPE).toUpperCase().replace(' ', '_'));
                }
            }
            return true;
        }
        return false;
    }
}
