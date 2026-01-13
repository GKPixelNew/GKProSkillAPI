package studio.magemonkey.fabled.dynamic.mechanic;

import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import studio.magemonkey.fabled.hook.MythicMobsHook;
import studio.magemonkey.fabled.hook.PluginChecker;

import java.util.List;

/**
 * Fabled © 2024
 * studio.magemonkey.fabled.dynamic.mechanic.AngryTowardsMechanic
 * <p>
 * Makes a mob angry towards a target entity. Supports two directions:
 * - Caster to Target: The caster (must be a mob) becomes angry at the first valid target
 * - Target to Caster: All valid mob targets become angry at the caster
 */
public class AngryTowardsMechanic extends MechanicComponent {
    private static final String DIRECTION = "direction";

    @Override
    public String getKey() {
        return "angry towards";
    }

    @Override
    public boolean execute(final LivingEntity caster,
                           final int level,
                           final List<LivingEntity> targets,
                           boolean force) {
        if (targets.isEmpty()) {
            return false;
        }

        String direction = settings.getString(DIRECTION, "Caster to Target").toLowerCase();

        if (direction.equals("target to caster")) {
            // Targets become angry at caster - useful for taunting/tanking
            boolean success = false;
            for (LivingEntity target : targets) {
                if (target instanceof Mob mob && target != caster && isValidTarget(target) && isValidTarget(caster)) {
                    setMobTarget(mob, caster);
                    success = true;
                }
            }
            return success;
        } else {
            // Caster becomes angry at target (default)
            if (!(caster instanceof Mob mob)) {
                return false;
            }

            // Find the first valid target
            LivingEntity target = null;
            for (LivingEntity t : targets) {
                if (isValidTarget(t) && t != caster) {
                    target = t;
                    break;
                }
            }

            if (target == null) {
                return false;
            }

            setMobTarget(mob, target);
            return true;
        }
    }

    /**
     * Sets the target for a mob, with special handling for MythicMobs and Iron Golems
     *
     * @param mob    the mob to set the target for
     * @param target the target entity
     */
    private void setMobTarget(Mob mob, LivingEntity target) {
        // MythicMobs integration - use threat system if available
        if (PluginChecker.isMythicMobsActive() && MythicMobsHook.isMonster(mob)) {
            MythicMobsHook.taunt(mob, target, 1);
            return;
        }

        // Iron Golems need special handling - they need to be set as not player-created to attack players
        if (mob instanceof IronGolem golem) {
            golem.setPlayerCreated(false);
        }

        mob.setTarget(target);
    }

    /**
     * Checks if an entity is a valid target (not null, not dead)
     *
     * @param entity the entity to check
     * @return true if valid, false otherwise
     */
    private boolean isValidTarget(LivingEntity entity) {
        return entity != null && !entity.isDead() && entity.isValid();
    }
}
