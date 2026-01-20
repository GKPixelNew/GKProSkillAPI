/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.condition.SkillCooldownCondition
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 MageMonkeyStudio
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package studio.magemonkey.fabled.dynamic.condition;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.api.player.PlayerSkill;

/**
 * A condition that checks the cooldown of a skill
 */
public class SkillCooldownCondition extends ConditionComponent {
    private static final String SKILL  = "skill";
    private static final String TYPE   = "type";
    private static final String TARGET = "target";
    private static final String MIN    = "min-value";
    private static final String MAX    = "max-value";

    @Override
    public boolean test(final LivingEntity caster, final int level, final LivingEntity target) {
        String checkTarget = settings.getString(TARGET, "caster").toLowerCase();
        LivingEntity entity = checkTarget.equals("target") ? target : caster;
        
        if (!(entity instanceof Player)) {
            return false;
        }

        String skillName = settings.getString(SKILL, "");
        String type = settings.getString(TYPE, "seconds").toLowerCase();
        double min = parseValues(caster, MIN, level, 0);
        double max = parseValues(caster, MAX, level, 999);

        PlayerData playerData = Fabled.getData((Player) entity);

        // Handle "all" skills case
        if (skillName.equalsIgnoreCase("all")) {
            for (PlayerSkill skillData : playerData.getSkills()) {
                if (!checkCooldown(skillData, type, min, max)) {
                    return false;
                }
            }
            return !playerData.getSkills().isEmpty();
        }

        // Handle specific skill
        PlayerSkill skillData = playerData.getSkill(skillName);
        if (skillData == null && !skillName.isEmpty()) {
            // Try current skill if specified skill doesn't exist
            skillData = playerData.getSkill(this.skill.getName());
        }
        if (skillData == null) {
            return false;
        }

        return checkCooldown(skillData, type, min, max);
    }

    private boolean checkCooldown(PlayerSkill skillData, String type, double min, double max) {
        double value;
        if (type.equals("percent")) {
            double totalCooldown = skillData.getCooldown();
            if (totalCooldown <= 0) {
                value = 0;
            } else {
                value = (skillData.getCooldownLeft() / totalCooldown) * 100;
            }
        } else {
            // seconds
            value = skillData.getCooldownLeft();
        }
        return value >= min && value <= max;
    }

    @Override
    public String getKey() {
        return "skill cooldown";
    }
}
