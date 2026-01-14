/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.condition.OwnerCondition
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
import studio.magemonkey.fabled.Fabled;

/**
 * A condition that checks if the target is the owner (summoner) of the caster entity.
 * This is useful for summoned entities to check if a target is their owner.
 */
public class OwnerCondition extends ConditionComponent {
    private static final String IS_OWNER = "is-owner";

    @Override
    public boolean test(final LivingEntity caster, final int level, final LivingEntity target) {
        boolean isOwner = settings.getBool(IS_OWNER, true);
        Object owner = Fabled.getMeta(caster, "sapi_summon_owner");
        if (owner == null) {
            return !isOwner;
        }
        boolean result = owner.equals(target);
        return isOwner ? result : !result;
    }

    @Override
    public String getKey() {
        return "owner";
    }
}
