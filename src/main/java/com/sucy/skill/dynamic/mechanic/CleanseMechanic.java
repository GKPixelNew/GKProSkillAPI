/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.CleanseMechanic
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.skill.dynamic.mechanic;

import com.google.common.collect.ImmutableSet;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerStatModifier;
import com.sucy.skill.api.util.FlagManager;
import com.sucy.skill.api.util.StatusFlag;
import com.sucy.skill.manager.AttributeManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Set;

/**
 * Cleanses a target of negative potion or status effects
 */
public class CleanseMechanic extends MechanicComponent {
    public static final Set<String> NEGATIVE_POTIONS = ImmutableSet.of(
            "BLINDNESS", "CONFUSION", "HUNGER", "LEVITATION", "POISON",
            "SLOW", "SLOW_DIGGING", "WEAKNESS", "WITHER", "GLOWING"
    );

    private static final String STATUS = "status";
    private static final String POTION = "potion";
    private static final String EXTINGUISH = "extinguish";
    private static final String RESET_NEGATIVE_STATS = "reset_negative_stats";

    @Override
    public String getKey() {
        return "cleanse";
    }

    /**
     * Executes the component
     *
     * @param caster  caster of the skill
     * @param level   level of the skill
     * @param targets targets to apply to
     *
     * @param force
     * @return true if applied to something, false otherwise
     */
    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        boolean worked = false;
        String status = settings.getString(STATUS, "None").toLowerCase();
        String potion = settings.getString(POTION).toUpperCase().replace(' ', '_');
        boolean extinguish = settings.getBool(EXTINGUISH, true);
        boolean resetNegativeStats = settings.getBool(RESET_NEGATIVE_STATS, true);
        PotionEffectType type = null;
        try {
            type = PotionEffectType.getByName(potion);
        } catch (Exception ex) {
            // Invalid potion type
        }

        for (LivingEntity target : targets) {
            if (status.equals("all")) {
                for (String flag : StatusFlag.NEGATIVE) {
                    if (FlagManager.hasFlag(target, flag)) {
                        FlagManager.removeFlag(target, flag);
                        worked = true;
                    }
                }
            } else if (FlagManager.hasFlag(target, status)) {
                FlagManager.removeFlag(target, status);
                worked = true;
            }

            if (potion.equals("ALL")) {
                for (PotionEffect p : target.getActivePotionEffects()) {
                    if (NEGATIVE_POTIONS.contains(p.getType().getName())) {
                        target.removePotionEffect(p.getType());
                        worked = true;
                    }
                }
            } else if (type != null && target.hasPotionEffect(type)) {
                target.removePotionEffect(type);
                worked = true;
            }

            if (extinguish && target.getFireTicks() > 0) {
                target.setFireTicks(0);
                worked = true;
            }

            if (resetNegativeStats && target instanceof Player player) {
                PlayerData playerData = SkillAPI.getPlayerData(player);
                for (PlayerStatModifier modifier : playerData.getStatModifiers(AttributeManager.MOVE_SPEED)) {
                    if (modifier.applyOn(0.2) < 0.2) {
                        playerData.removeStatModifier(modifier.getUUID(), true);
                        worked = true;
                    }
                }
            }
        }
        return worked;
    }
}
