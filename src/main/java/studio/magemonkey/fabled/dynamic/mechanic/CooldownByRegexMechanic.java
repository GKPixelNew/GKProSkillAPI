package studio.magemonkey.fabled.dynamic.mechanic;

import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.api.player.PlayerSkill;

/**
 * Lowers the cooldowns of the caster's skills matching a regex
 */
public class CooldownByRegexMechanic extends MechanicComponent {
    private static final String REGEX = "regex";
    private static final String TYPE  = "type";
    private static final String VALUE = "value";

    @Override
    public String getKey() {
        return "cooldown by regex";
    }

    /**
     * Executes the component
     *
     * @param caster  caster of the skill
     * @param level   level of the skill
     * @param targets targets to apply to
     * @param force
     * @return true if applied to something, false otherwise
     */
    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        if (!(caster instanceof Player)) {
            return false;
        }

        String regex = settings.getString(REGEX, ".*");
        String type  = settings.getString(TYPE, "all").toLowerCase();
        double value = parseValues(caster, VALUE, level, 0);

        PlayerData playerData = Fabled.getData((Player) caster);
        Pattern pattern;
        try {
            pattern = Pattern.compile(regex);
        } catch (Exception e) {
            return false;
        }

        boolean worked = false;
        for (PlayerSkill data : playerData.getSkills()) {
            if (pattern.matcher(data.getData().getName()).matches()) {
                subtractCooldown(type, data, value);
                worked = true;
            }
        }
        return worked;
    }

    private void subtractCooldown(String type, PlayerSkill data, double value) {
        Bukkit.getScheduler().runTaskLater(Fabled.inst(), () -> {
            if (type.equals("percent")) data.subtractCooldown(value * data.getCooldownLeft() / 100);
            else data.subtractCooldown(value);
        }, 1L);
    }
}
