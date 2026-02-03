/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.mechanic.FlagMechanic
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

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.enums.Operation;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.api.player.PlayerStatModifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Applies a stat modifier to each target (players use Fabled stats, mobs use vanilla attributes)
 */
public class StatMechanic extends MechanicComponent {
    private static final String KEY       = "key";
    private static final String OPERATION = "operation";
    private static final String AMOUNT    = "amount";
    private static final String SECONDS   = "seconds";
    private static final String STACKABLE = "stackable";

    private final Map<Integer, Map<String, StatTask>>   tasks    = new HashMap<>();
    private final Map<Integer, Map<String, MobStatTask>> mobTasks = new HashMap<>();

    @Override
    public String getKey() {
        return "stat";
    }

    @Override
    protected void doCleanUp(final LivingEntity user) {
        final Map<String, StatTask> casterTasks = tasks.remove(user.getEntityId());
        if (casterTasks != null) {
            casterTasks.values().forEach(StatTask::stop);
        }
        final Map<String, MobStatTask> casterMobTasks = mobTasks.remove(user.getEntityId());
        if (casterMobTasks != null) {
            casterMobTasks.values().forEach(MobStatTask::stop);
        }
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        String key = settings.getString(KEY, "");
        if (targets.isEmpty()) {
            return false;
        }

        final Map<String, StatTask>    casterTasks    = tasks.computeIfAbsent(caster.getEntityId(), HashMap::new);
        final Map<String, MobStatTask> casterMobTasks = mobTasks.computeIfAbsent(caster.getEntityId(), HashMap::new);
        final double                   amount         = parseValues(caster, AMOUNT, level, 5);
        final double                   seconds        = parseValues(caster, SECONDS, level, 3.0);
        final boolean                  stackable      = settings.getBool(STACKABLE, false);
        final int                      ticks          = (int) (seconds * 20);
        final String                   operation      = settings.getString(OPERATION, "MULTIPLY_PERCENTAGE");

        boolean worked = false;
        for (LivingEntity target : targets) {
            if (target instanceof Player player) {
                // Player: use Fabled stat system
                worked = true;
                final PlayerData data = Fabled.getData(player);
                PlayerStatModifier modifier = new PlayerStatModifier("fabled.mechanic.stat_mechanic", amount,
                        Operation.valueOf(operation), false);

                if (casterTasks.containsKey(data.getPlayerName()) && !stackable) {
                    final StatTask old = casterTasks.remove(data.getPlayerName());

                    data.removeStatModifier(old.modifier.getUUID(), false);

                    data.addStatModifier(key, modifier, true);

                    old.cancel();
                } else {
                    data.addStatModifier(key, modifier, true);
                }

                final StatTask task = new StatTask(caster.getEntityId(), data, modifier);
                casterTasks.put(data.getPlayerName(), task);
                if (ticks >= 0) {
                    Fabled.schedule(task, ticks);
                }
            } else if (target instanceof Attributable attributable) {
                // Mob: use vanilla attribute system
                Attribute attribute = resolveAttribute(key);
                if (attribute == null) {
                    continue;
                }
                
                AttributeInstance attrInstance = attributable.getAttribute(attribute);
                if (attrInstance == null) {
                    // Try to register the attribute if possible
                    try {
                        attributable.registerAttribute(attribute);
                        attrInstance = attributable.getAttribute(attribute);
                    } catch (Exception ignored) {
                    }
                }
                if (attrInstance == null) {
                    continue;
                }

                worked = true;
                String targetKey = target.getUniqueId().toString();
                
                // Convert operation to Bukkit's AttributeModifier.Operation
                AttributeModifier.Operation attrOp = operation.equals("MULTIPLY_PERCENTAGE") 
                        ? AttributeModifier.Operation.MULTIPLY_SCALAR_1 
                        : AttributeModifier.Operation.ADD_NUMBER;
                
                NamespacedKey modifierKey = new NamespacedKey(Fabled.inst(), "stat_mechanic_" + UUID.randomUUID());
                AttributeModifier attrModifier = new AttributeModifier(modifierKey, amount, attrOp);

                if (casterMobTasks.containsKey(targetKey) && !stackable) {
                    final MobStatTask old = casterMobTasks.remove(targetKey);
                    old.stop();
                }

                attrInstance.addModifier(attrModifier);

                final MobStatTask task = new MobStatTask(caster.getEntityId(), target, attribute, attrModifier);
                casterMobTasks.put(targetKey, task);
                if (ticks >= 0) {
                    Fabled.schedule(task, ticks);
                }
            }
        }
        return worked;
    }

    /**
     * Resolves a stat key to a Bukkit Attribute.
     * Supports both simple names (e.g., "scale", "max_health") and full namespaced keys.
     */
    private Attribute resolveAttribute(String key) {
        if (key == null || key.isEmpty()) {
            return null;
        }
        
        // Normalize the key
        String normalizedKey = key.toLowerCase().replace("-", "_").replace(" ", "_");
        
        // Try direct match with minecraft namespace
        try {
            NamespacedKey nsKey = NamespacedKey.minecraft(normalizedKey);
            Attribute attr = Registry.ATTRIBUTE.get(nsKey);
            if (attr != null) return attr;
        } catch (Exception ignored) {
        }
        
        // Try with "generic." prefix for common attributes
        try {
            NamespacedKey nsKey = NamespacedKey.minecraft("generic." + normalizedKey);
            Attribute attr = Registry.ATTRIBUTE.get(nsKey);
            if (attr != null) return attr;
        } catch (Exception ignored) {
        }
        
        // Try parsing as full namespaced key
        try {
            NamespacedKey nsKey = NamespacedKey.fromString(normalizedKey);
            if (nsKey != null) {
                Attribute attr = Registry.ATTRIBUTE.get(nsKey);
                if (attr != null) return attr;
            }
        } catch (Exception ignored) {
        }
        
        return null;
    }

    private class StatTask extends BukkitRunnable {

        private final PlayerData         data;
        private final PlayerStatModifier modifier;
        private final int                id;
        private       boolean            running = false;
        private       boolean            stopped = false;

        StatTask(int id, PlayerData data, PlayerStatModifier modifier) {
            this.id = id;
            this.data = data;
            this.modifier = modifier;
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
            data.removeStatModifier(modifier.getUUID(), true);
            if (tasks.containsKey(id)) {
                tasks.get(id).remove(data.getPlayerName());
            }
            running = false;
        }
    }

    private class MobStatTask extends BukkitRunnable {

        private final LivingEntity        target;
        private final Attribute           attribute;
        private final AttributeModifier   modifier;
        private final int                 id;
        private       boolean             running = false;
        private       boolean             stopped = false;

        MobStatTask(int id, LivingEntity target, Attribute attribute, AttributeModifier modifier) {
            this.id = id;
            this.target = target;
            this.attribute = attribute;
            this.modifier = modifier;
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
            if (target instanceof Attributable attributable) {
                AttributeInstance attrInstance = attributable.getAttribute(attribute);
                if (attrInstance != null) {
                    attrInstance.removeModifier(modifier);
                }
            }
            if (mobTasks.containsKey(id)) {
                mobTasks.get(id).remove(target.getUniqueId().toString());
            }
            running = false;
        }
    }
}
