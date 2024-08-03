/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.mechanic.DamageMechanic
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
package studio.magemonkey.fabled.dynamic.mechanic;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;
import java.util.Locale;

/**
 * Deals damage to each target
 */
public class DamageMechanic extends MechanicComponent {
    private static final String TYPE       = "type";
    private static final String DAMAGE     = "value";
    private static final String TRUE       = "true";
    private static final String CLASSIFIER = "classifier";
    private static final String KNOCKBACK  = "knockback";
    private static final String CAUSE      = "cause";

    @Override
    public String getKey() {
        return "damage";
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        String  pString        = settings.getString(TYPE, "damage").toLowerCase();
        boolean percent        = pString.equals("multiplier") || pString.equals("percent");
        boolean missing        = pString.equals("percent missing");
        boolean left           = pString.equals("percent left");
        boolean trueDmg        = settings.getBool(TRUE, false);
        double  damage         = parseValues(caster, DAMAGE, level, 1.0);
        boolean knockback      = settings.getBool(KNOCKBACK, true);
        String  classification = settings.getString(CLASSIFIER, "default");
        if (damage < 0) {
            return false;
        }
        for (LivingEntity target : targets) {
            if (target.isDead()) {
                continue;
            }

            double amount = damage;
            if (percent) {
                amount = damage * target.getMaxHealth() / 100;
            } else if (missing) {
                amount = damage * (target.getMaxHealth() - target.getHealth()) / 100;
            } else if (left) {
                amount = damage * target.getHealth() / 100;
            }
            if (trueDmg) {
                skill.trueDamage(target, amount, caster);
            } else {
                skill.damage(target,
                        amount,
                        caster,
                        classification,
                        knockback,
                        EntityDamageEvent.DamageCause.valueOf(settings.getString(CAUSE, "Custom")
                                .toUpperCase(Locale.US)
                                .replace(' ', '_')));
            }
        }
        return !targets.isEmpty();
    }
}
