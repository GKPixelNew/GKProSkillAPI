package studio.magemonkey.fabled.dynamic.target;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import studio.magemonkey.fabled.Fabled;

import java.util.LinkedList;
import java.util.List;

public class AngryAt extends TargetComponent {
    private static final String TYPE = "type";

    @Override
    public String getKey() {
        return "angry at";
    }

    @Override
    public List<LivingEntity> getTargets(LivingEntity caster, int level, List<LivingEntity> targets) {
        List<LivingEntity> result = new LinkedList<>();
        if (caster instanceof Mob) {
            LivingEntity target = ((Mob) caster).getTarget();
            if (target != null) {
                result.add(target);
            }
        }
        return result;
    }
}