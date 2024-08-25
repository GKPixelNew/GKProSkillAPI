package studio.magemonkey.fabled.dynamic.mechanic;

import lombok.extern.slf4j.Slf4j;
import org.bukkit.entity.LivingEntity;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.dynamic.DynamicSkill;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class AbortSkillMechanic extends MechanicComponent {
    public static final String TYPE = "type";
    public static final String SKILL = "skill";
    public static final String SELF = "self";

    @Override
    public String getKey() {
        return "abort skill";
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        final String type = settings.getString(TYPE, "all").toLowerCase();
        final String specific = settings.getString(SKILL, "");
        final boolean self = settings.getBool(SELF, true);
        final List<DynamicSkill> skills = switch (type) {
            case "all" -> Fabled.getSkills().values().stream()
                    .filter(s -> s instanceof DynamicSkill)
                    .map(s -> (DynamicSkill) s)
                    .toList();
            case "allbutcurrent" -> Fabled.getSkills().values().stream()
                    .filter(s -> s instanceof DynamicSkill)
                    .filter(s -> !s.getName().equals(skill.getName()))
                    .map(s -> (DynamicSkill) s)
                    .toList();
            case "current" -> List.of(skill);
            case "specific" -> Stream.of(Fabled.getSkill(specific))
                    .filter(s -> s instanceof DynamicSkill)
                    .map(s -> (DynamicSkill) s)
                    .toList();
            default -> {
                log.error("Invalid abort type: {}", type);
                yield List.of();
            }
        };

        if (self) targets = List.of(caster);
        for (DynamicSkill s : skills) {
            for (LivingEntity target : targets) {
                for (RepeatMechanic.RepeatTask task : RepeatMechanic.tasks.get(target.getEntityId())) {
                    if (s.getName().equals(task.getSkill().getName())) {
                        task.cancel();
                    }
                }
                for (DelayMechanic.DelayTask task : DelayMechanic.tasks.get(target.getUniqueId())) {
                    if (s.getName().equals(task.getSkill().getName())) {
                        task.cancel();
                    }
                }
                s.stopEffects(target);
                s.initialize(target, level);
            }
        }
        return true;
    }
}



