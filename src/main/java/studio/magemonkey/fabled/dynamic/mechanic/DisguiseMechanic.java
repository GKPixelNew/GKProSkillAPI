/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.mechanic.DisguiseMechanic
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 MageMonkeyStudio
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import studio.magemonkey.fabled.api.util.FlagManager;
import studio.magemonkey.fabled.dynamic.TempEntity;
import studio.magemonkey.fabled.hook.DisguiseHook;
import studio.magemonkey.fabled.hook.PluginChecker;
import studio.magemonkey.fabled.listener.MechanicListener;

import java.util.List;
import java.util.Locale;

/**
 * Disguises each target
 */
public class DisguiseMechanic extends MechanicComponent {
    private static final String TYPE     = "type";
    private static final String MOB      = "mob";
    private static final String ADULT    = "adult";
    private static final String PLAYER   = "player";
    private static final String MISC     = "misc";
    private static final String DATA     = "data";
    private static final String MATERIAL = "mat";
    private static final String DURATION = "duration";
    private static final String CHANGE_NAME = "change_name";

    @Override
    public String getKey() {
        return "disguise";
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
        if (!PluginChecker.isDisguiseActive()) {
            return false;
        }

        String type = settings.getString(TYPE);
        boolean changeName = settings.getBool(CHANGE_NAME, false);

        // Mob disguises
        if (type.equalsIgnoreCase("mob")) {
            for (LivingEntity target : targets) {
                if (!(target instanceof TempEntity)) {
                    DisguiseHook.disguiseMob(target, settings.getString(MOB, "Zombie"), settings.getBool(ADULT, true));
                }
            }
        }

        // Player disguises
        else if (type.equalsIgnoreCase("player")) {
            for (LivingEntity target : targets) {
                if (!(target instanceof TempEntity)) {
                    DisguiseHook.disguisePlayer(
                            target,
                            filter(caster, target, settings.getString(PLAYER, "Eniripsa96")), changeName);
                }
            }
        }

        // Miscellaneous disguises
        else if (type.equalsIgnoreCase("misc")) {
            for (LivingEntity target : targets) {
                if (!(target instanceof TempEntity)) {
                    String dataType = settings.getString(MISC, "Painting");
                    if (dataType.equals("Dropped Item") || dataType.equals("Falling Block")) {
                        DisguiseHook.disguiseMisc(target,
                                dataType,
                                Material.valueOf(settings.getString(MATERIAL, "Anvil")
                                        .toUpperCase(Locale.US)
                                        .replace(" ", "_")));
                    } else
                        DisguiseHook.disguiseMisc(target, dataType, settings.getInt(DATA, 0));
                }
            }
        }

        // Invalid type
        else {
            return false;
        }

        // Apply Flag duration
        int ticks = (int) (parseValues(caster, DURATION, level, -1) * 20);
        for (LivingEntity target : targets) {
            if (!(target instanceof TempEntity)) {
                FlagManager.addFlag(target, MechanicListener.DISGUISE_KEY, ticks);
            }
        }

        return targets.size() > 0;
    }
}