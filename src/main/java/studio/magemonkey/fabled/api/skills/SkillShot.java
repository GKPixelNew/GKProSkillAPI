/**
 * Fabled
 * studio.magemonkey.fabled.api.skills.SkillShot
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
package studio.magemonkey.fabled.api.skills;

import org.bukkit.entity.LivingEntity;

/**
 * <p>Interface for skills that can be cast without a direct target</p>
 * <p>Common applications would include firing projectiles, self-targeting
 * skills, and AOE abilities around yourself or where you are looking</p>
 */
public interface SkillShot {
    /**
     * Casts the skill
     *
     * @param user  user of the skill
     * @param level skill level
     * @param force force the cast
     * @return true if could cast, false otherwise
     */
    boolean cast(LivingEntity user, int level, boolean force);

    /**
     * Casts the skill
     *
     * @param user  user of the skill
     * @param level skill level
     * @return true if could cast, false otherwise
     */
    boolean cast(LivingEntity user, int level);
}
