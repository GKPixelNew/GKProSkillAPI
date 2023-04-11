/**
 * SkillAPI
 * com.sucy.skill.dynamic.condition.ManaCondition
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2014 Steven Sucy
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
package com.sucy.skill.dynamic.condition;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class ManaCondition extends ConditionComponent {
    private static final String TYPE = "type";
    private static final String MIN = "min-value";
    private static final String MAX = "max-value";

    @Override
    public boolean test(final LivingEntity caster, final int level, final LivingEntity target) {
        if (!(target instanceof Player)) {
            return false;
        }

        final String      type  = settings.getString(TYPE).toLowerCase();
        final double      min   = parseValues(caster, MIN, level, 0);
        final double      max   = parseValues(caster, MAX, level, 99);
        final PlayerData  data  = SkillAPI.getPlayerData((Player) target);
        final PlayerSkill skill = getSkillData(caster);
        final double      mana  = data.getMana();

        double value;
        switch (type) {
            case "difference percent":
                value = (mana - skill.getPlayerData().getMana()) * 100 / skill.getPlayerData().getMana();
                break;
            case "difference":
                value = mana - skill.getPlayerData().getMana();
                break;
            case "percent":
                value = mana * 100 / data.getMaxMana();
                break;
            default:
                value = mana;
                break;
        }
        return value >= min && value <= max;
    }

    @Override
    public String getKey() {
        return "mana";
    }
}
