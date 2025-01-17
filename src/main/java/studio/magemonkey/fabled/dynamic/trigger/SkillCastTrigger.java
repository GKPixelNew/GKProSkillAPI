package studio.magemonkey.fabled.dynamic.trigger;

import org.bukkit.entity.LivingEntity;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;
import studio.magemonkey.fabled.api.event.PlayerCastSkillEvent;
import studio.magemonkey.fabled.api.player.PlayerClass;

import java.util.List;
import java.util.stream.Collectors;

public class SkillCastTrigger implements Trigger<PlayerCastSkillEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "SKILL_CAST";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<PlayerCastSkillEvent> getEvent() {
        return PlayerCastSkillEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(final PlayerCastSkillEvent event, final int level, final Settings settings) {
        List<String> classes = settings.getStringList("allowed-classes")
                .stream()
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        List<String> skills = settings.getStringList("allowed-skills")
                .stream()
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        List<String> blackClasses = classes.stream().filter(c -> c.startsWith("!"))
                .map(c -> c.substring(1))
                .toList();
        List<String> blackSkills = skills.stream().filter(c -> c.startsWith("!"))
                .map(c -> c.substring(1))
                .toList();
        classes = classes.stream().filter(c -> !c.startsWith("!")).toList();
        skills = skills.stream().filter(c -> !c.startsWith("!")).toList();

        String skillName = event.getSkill().getData().getName();
        PlayerClass mainClass = event.getPlayerData().getMainClass();
        String className = mainClass == null ? "" : event.getPlayerData().getMainClass().getData().getName();
        if (!skills.isEmpty() && !skills.contains(skillName)
                || blackSkills.contains(skillName)) {
            return false;
        }

        if (!classes.isEmpty() && !classes.contains(className)
                || blackClasses.contains(className)) {
            return false;
        }

        boolean cancelEvent = settings.getBool("cancel");
        if (cancelEvent) {
            event.setCancelled(true);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(final PlayerCastSkillEvent event, final CastData data) {
        data.put("api-skill", event.getSkill().getData().getName());
        data.put("api-mana", event.getManaCost());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(final PlayerCastSkillEvent event) {
        return event.getPlayer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(final PlayerCastSkillEvent event, final Settings settings) {
        return event.getPlayer();
    }

}
