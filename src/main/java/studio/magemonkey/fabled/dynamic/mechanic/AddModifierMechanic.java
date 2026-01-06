/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.mechanic.AddModifierMechanic
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
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.enums.Operation;
import studio.magemonkey.fabled.api.player.PlayerAttributeModifier;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.api.player.PlayerStatModifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Adds a stat or attribute modifier to targets that can be removed later by key
 */
public class AddModifierMechanic extends MechanicComponent {
    private static final String KEY           = "key";
    private static final String TYPE          = "type";
    private static final String STAT_KEY      = "stat-key";
    private static final String ATTRIBUTE_KEY = "attribute-key";
    private static final String OPERATION     = "operation";
    private static final String AMOUNT        = "amount";
    private static final String SECONDS       = "seconds";
    private static final String STACKABLE     = "stackable";

    private final Map<Integer, Map<String, ModifierTask>> tasks = new HashMap<>();

    @Override
    public String getKey() {
        return "add modifier";
    }

    @Override
    protected void doCleanUp(final LivingEntity user) {
        final Map<String, ModifierTask> casterTasks = tasks.remove(user.getEntityId());
        if (casterTasks != null) {
            casterTasks.values().forEach(ModifierTask::stop);
        }
    }

    /**
     * Reads a key that might be stored as a string or a list
     */
    private String readTargetKey(String settingKey) {
        // Try as list first (dropdown selects often store as list)
        List<String> list = settings.getStringList(settingKey);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        
        // Try as string
        String value = settings.getString(settingKey, "");
        if (value != null && !value.isBlank() && !value.equals("[]")) {
            // Handle case where it's stored as "[value]" string
            if (value.startsWith("[") && value.endsWith("]")) {
                value = value.substring(1, value.length() - 1);
            }
            return value;
        }
        
        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        if (targets.isEmpty()) {
            return false;
        }

        final String  modifierKey = settings.getString(KEY, "default");
        final String  type        = settings.getString(TYPE, "STAT");
        final boolean isStat      = type.equalsIgnoreCase("STAT");
        final String  targetKey   = isStat ? readTargetKey(STAT_KEY) : readTargetKey(ATTRIBUTE_KEY);
        final String  operation   = settings.getString(OPERATION, "ADD_NUMBER");
        final double  amount      = parseValues(caster, AMOUNT, level, 5);
        final double  seconds     = parseValues(caster, SECONDS, level, -1);
        final boolean stackable   = settings.getBool(STACKABLE, false);
        final boolean permanent   = seconds < 0;
        final int     ticks       = permanent ? -1 : (int) (seconds * 20);

        if (targetKey.isEmpty()) {
            return false;
        }

        // Validate attribute key exists if it's an attribute modifier
        if (!isStat && Fabled.getAttributesManager().getAttribute(targetKey) == null) {
            return false;
        }

        final Map<String, ModifierTask> casterTasks = tasks.computeIfAbsent(caster.getEntityId(), HashMap::new);

        boolean worked = false;
        for (LivingEntity target : targets) {
            if (target instanceof Player) {
                worked = true;
                final PlayerData data    = Fabled.getData((Player) target);
                final String     taskKey = data.getPlayerName() + ":" + modifierKey;

                // Handle non-stackable: remove old modifier with same key
                if (!stackable && casterTasks.containsKey(taskKey)) {
                    final ModifierTask old = casterTasks.remove(taskKey);
                    if (old.isStat) {
                        data.removeStatModifier(old.modifierUUID, false);
                    } else {
                        data.removeAttributeModifier(old.modifierUUID, false);
                    }
                    old.cancel();
                }

                UUID modifierUUID;
                if (isStat) {
                    PlayerStatModifier modifier = new PlayerStatModifier(
                            modifierKey,
                            amount,
                            Operation.valueOf(operation),
                            permanent
                    );
                    modifierUUID = modifier.getUUID();
                    data.addStatModifier(targetKey, modifier, true);
                } else {
                    PlayerAttributeModifier modifier = new PlayerAttributeModifier(
                            modifierKey,
                            amount,
                            Operation.valueOf(operation),
                            permanent
                    );
                    modifierUUID = modifier.getUUID();
                    data.addAttributeModifier(targetKey, modifier, true);
                }

                // Schedule removal task if not permanent
                if (!permanent) {
                    final ModifierTask task = new ModifierTask(
                            caster.getEntityId(),
                            data,
                            modifierUUID,
                            modifierKey,
                            isStat
                    );
                    casterTasks.put(taskKey, task);
                    Fabled.schedule(task, ticks);
                }
            }
        }
        return worked;
    }

    private class ModifierTask extends BukkitRunnable {
        private final PlayerData data;
        private final UUID       modifierUUID;
        private final String     modifierKey;
        private final boolean    isStat;
        private final int        casterId;
        private       boolean    running = false;
        private       boolean    stopped = false;

        ModifierTask(int casterId, PlayerData data, UUID modifierUUID, String modifierKey, boolean isStat) {
            this.casterId = casterId;
            this.data = data;
            this.modifierUUID = modifierUUID;
            this.modifierKey = modifierKey;
            this.isStat = isStat;
        }

        public void stop() {
            if (!stopped) {
                stopped = true;
                run();
                if (running) {
                    cancel();
                }
            }
        }

        @Override
        public BukkitTask runTaskLater(final Plugin plugin, final long delay) {
            running = true;
            return super.runTaskLater(plugin, delay);
        }

        @Override
        public void run() {
            if (isStat) {
                data.removeStatModifier(modifierUUID, true);
            } else {
                data.removeAttributeModifier(modifierUUID, true);
            }
            if (tasks.containsKey(casterId)) {
                tasks.get(casterId).remove(data.getPlayerName() + ":" + modifierKey);
            }
            running = false;
        }
    }
}
