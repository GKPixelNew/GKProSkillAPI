package studio.magemonkey.fabled.dynamic.mechanic;

import org.bukkit.entity.LivingEntity;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.skills.Skill;
import studio.magemonkey.fabled.dynamic.DynamicSkill;

import java.util.List;

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
        switch (type) {
            case "all" -> {
                for (Skill s : Fabled.getSkills().values()) {
                    if (s instanceof DynamicSkill dynamicSkill) {
                        if (self) {
                            for (RepeatMechanic.RepeatTask task : RepeatMechanic.tasks.get(caster.getEntityId())) {
                                if (dynamicSkill.getName().equals(task.getSkill().getName())) {
                                    task.cancel();
                                }
                                ;
                            }
                            dynamicSkill.stopEffects(caster);
                            dynamicSkill.initialize(caster, level);
                        } else {
                            for (LivingEntity target : targets) {
                                for (RepeatMechanic.RepeatTask task : RepeatMechanic.tasks.get(target.getEntityId())) {
                                    if (dynamicSkill.getName().equals(task.getSkill().getName())) {
                                        task.cancel();
                                    }
                                }
                                dynamicSkill.stopEffects(target);
                                dynamicSkill.initialize(target, level);
                            }
                        }

                    }
                }
            }
            case "current" -> {

                if (self) {
                    for (RepeatMechanic.RepeatTask task : RepeatMechanic.tasks.get(caster.getEntityId())) {
                        if (skill.getName().equals(task.getSkill().getName())) {
                            task.cancel();
                        }
                        ;
                    }
                    skill.stopEffects(caster);
                    skill.initialize(caster, level);
                } else {
                    for (LivingEntity target : targets) {
                        for (RepeatMechanic.RepeatTask task : RepeatMechanic.tasks.get(target.getEntityId())) {
                            if (skill.getName().equals(task.getSkill().getName())) {
                                task.cancel();
                            }
                            ;
                        }
                        skill.stopEffects(target);
                        skill.initialize(target, level);
                    }
                }

            }
            case "specific" -> {
                if (Fabled.inst().getSkill(specific) instanceof DynamicSkill specificSkill) {
                    if (self) {
                        for (RepeatMechanic.RepeatTask task : RepeatMechanic.tasks.get(caster.getEntityId())) {
                            if (specific.equals(task.getSkill().getName())) {
                                task.cancel();
                            }
                            ;
                        }
                        specificSkill.stopEffects(caster);
                        specificSkill.initialize(caster, level);
                    } else {
                        for (LivingEntity target : targets) {
                            for (RepeatMechanic.RepeatTask task : RepeatMechanic.tasks.get(target.getEntityId())) {
                                if (specific.equals(task.getSkill().getName())) {
                                    task.cancel();
                                }
                            }
                            specificSkill.stopEffects(target);
                            specificSkill.initialize(target, level);
                        }
                    }

                }
            }
        }
        skill.stopEffects(caster);
        skill.initialize(caster, level);

        return true;
    }
}



