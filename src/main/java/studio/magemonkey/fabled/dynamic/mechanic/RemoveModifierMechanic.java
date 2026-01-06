/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.mechanic.RemoveModifierMechanic
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
import org.bukkit.entity.Player;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerData;

import java.util.List;

/**
 * Removes stat or attribute modifiers from targets by key
 */
public class RemoveModifierMechanic extends MechanicComponent {
    private static final String KEY    = "key";
    private static final String TYPE   = "type";
    private static final String AMOUNT = "amount";

    @Override
    public String getKey() {
        return "remove modifier";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        if (targets.isEmpty()) {
            return false;
        }

        final String modifierKey = settings.getString(KEY, "default");
        final String type        = settings.getString(TYPE, "STAT");
        final int    amount      = (int) parseValues(caster, AMOUNT, level, -1);
        final boolean isStat     = type.equalsIgnoreCase("STAT");

        boolean worked = false;
        for (LivingEntity target : targets) {
            if (target instanceof Player) {
                final PlayerData data = Fabled.getData((Player) target);
                
                int removed;
                if (isStat) {
                    removed = data.removeStatModifiersByName(modifierKey, amount, true);
                } else {
                    removed = data.removeAttributeModifiersByName(modifierKey, amount, true);
                }
                
                if (removed > 0) {
                    worked = true;
                }
            }
        }
        return worked;
    }
}
