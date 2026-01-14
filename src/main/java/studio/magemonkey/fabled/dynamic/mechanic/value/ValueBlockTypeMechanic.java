/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.mechanic.value.ValueBlockTypeMechanic
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
package studio.magemonkey.fabled.dynamic.mechanic.value;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.LivingEntity;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.dynamic.mechanic.MechanicComponent;

import java.util.List;

/**
 * Stores the block type (BlockData) at the target's location into cast data
 */
public class ValueBlockTypeMechanic extends MechanicComponent {
    private static final String KEY               = "key";
    private static final String SAVE              = "save";
    private static final String CHECK_BLOCK_BELOW = "check-block-below";

    @Override
    public String getKey() {
        return "value block type";
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
        if (!settings.has(KEY) || targets.isEmpty()) {
            return false;
        }

        String   key            = settings.getString(KEY);
        boolean  checkBelow     = settings.getBool(CHECK_BLOCK_BELOW, false);
        CastData data           = DynamicSkill.getCastData(caster);
        
        Location targetLocation = targets.get(0).getLocation();
        if (checkBelow) {
            targetLocation = targetLocation.clone().subtract(0, 1, 0);
        }
        
        Block     block     = targetLocation.getBlock();
        BlockData blockData = block.getBlockData();
        
        data.put(key, blockData);
        
        if (settings.getBool(SAVE, false) && caster instanceof OfflinePlayer) {
            // Store as string for persistence since BlockData is not directly serializable
            Fabled.getData((OfflinePlayer) caster).setPersistentData(key, blockData.getAsString());
        }
        
        return true;
    }
}
