/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.mechanic.GKSummonMechanic
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

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.scoreboard.Scoreboard;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.skills.PassiveSkill;
import studio.magemonkey.fabled.api.skills.Skill;
import studio.magemonkey.fabled.dynamic.target.RememberTarget;

import java.util.ArrayList;
import java.util.List;

/**
 * Summons a configurable entity with full attribute control
 */
public class GKSummonMechanic extends MechanicComponent {

    private static final String ENTITY = "entity";
    private static final String HEALTH = "health";
    private static final String DAMAGE = "damage";
    private static final String KNOCKBACK = "knockback";
    private static final String WALK_SPEED = "walk_speed";
    private static final String FLY_SPEED = "fly_speed";
    private static final String FOLLOW_RANGE = "follow_range";
    private static final String ARMOR = "armor";
    private static final String ADULT = "adult";
    private static final String AMOUNT = "amount";
    private static final String DURATION = "duration";
    private static final String SKILLS = "skills";
    private static final String TARGET = "target"; // for shulker bullet
    private static final String RIDE = "ride";
    private static final String COPY_SCOREBOARD_TEAM = "copy-scoreboard-team";
    private static final String INVISIBILITY = "invisibility";
    private static final String GRAVITY = "gravity";
    private static final String CAN_DAMAGE_OWNER = "can-damage-owner";

    @Override
    public String getKey() {
        return "gksummon";
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {

        List<LivingEntity> entities = new ArrayList<>();
        double amount = parseValues(caster, AMOUNT, level, 1);

        List<String> skills = settings.getStringList(SKILLS);
        boolean copyScoreboardTeam = settings.getBool(COPY_SCOREBOARD_TEAM, false);
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        for (LivingEntity target : targets) {
            for (int i = 0; i < amount; i++) {
                Entity entity = target.getWorld().spawnEntity(target.getLocation(), EntityType.valueOf(settings.getString(ENTITY).replace(' ', '_').toUpperCase()));

                // Copy scoreboard team from target if enabled
                if (copyScoreboardTeam && scoreboard.getEntityTeam(target) != null) {
                    scoreboard.getEntityTeam(target).addEntity(entity);
                }

                if (entity instanceof Monster monster) {
                    monster.setAI(true);
                    monster.setTarget(null);
                }

                if (entity instanceof ArmorStand armorStand) {
                    armorStand.setVisible(!settings.getBool(INVISIBILITY, false));
                    armorStand.setGravity(settings.getBool(GRAVITY, true));
                }

                if (entity instanceof Tameable tameable)
                    tameable.setOwner((AnimalTamer) target);

                if (entity instanceof Attributable ae) {
                    ae.registerAttribute(Attribute.ATTACK_DAMAGE);
                    ae.registerAttribute(Attribute.ATTACK_KNOCKBACK);
                    ae.registerAttribute(Attribute.MOVEMENT_SPEED);
                    ae.registerAttribute(Attribute.FLYING_SPEED);
                    ae.registerAttribute(Attribute.FOLLOW_RANGE);
                    ae.registerAttribute(Attribute.MAX_HEALTH);
                    ae.registerAttribute(Attribute.ARMOR);

                    ae.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(parseValues(target, DAMAGE, level, 2.0));
                    ae.getAttribute(Attribute.ATTACK_KNOCKBACK).setBaseValue(parseValues(target, KNOCKBACK, level, 1.0));
                    if (parseValues(target, WALK_SPEED, level, -1) > 0)
                        ae.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(parseValues(target, WALK_SPEED, level, -1));
                    if (parseValues(target, FLY_SPEED, level, -1) > 0)
                        ae.getAttribute(Attribute.FLYING_SPEED).setBaseValue(parseValues(target, FLY_SPEED, level, -1));
                    if (parseValues(target, FOLLOW_RANGE, level, -1) > 0)
                        ae.getAttribute(Attribute.FOLLOW_RANGE).setBaseValue(parseValues(target, FOLLOW_RANGE, level, 10));
                    ae.getAttribute(Attribute.MAX_HEALTH).setBaseValue(parseValues(target, HEALTH, level, 20.0));
                    ae.getAttribute(Attribute.ARMOR).setBaseValue(parseValues(target, ARMOR, level, 0));
                }

                if (entity instanceof LivingEntity le) {
                    le.setHealth(parseValues(target, HEALTH, level, 20.0));
                    if (le instanceof Ageable ageable) {
                        if (settings.getBool(ADULT, true))
                            ageable.setAdult();
                        else
                            ageable.setBaby();
                    }
                    // Setup skills
                    for (String skillName : skills) {
                        Skill skill = Fabled.getSkill(skillName);
                        if (skill instanceof PassiveSkill) {
                            ((PassiveSkill) skill).initialize(le, level);
                        }
                    }

                    Fabled.setMeta(le, "sapi_wolf_skills", skills);
                    Fabled.setMeta(le, "sapi_wolf_level", level);
                    Fabled.setMeta(le, "sapi_summon_owner", caster);
                    Fabled.setMeta(le, "sapi_can_damage_owner", settings.getBool(CAN_DAMAGE_OWNER, true));

                    entities.add(le);
                }

                if (entity instanceof ShulkerBullet shulkerBullet && !settings.getString(TARGET, "").isEmpty()) {
                    List<LivingEntity> remembers = RememberTarget.remember(target, settings.getString(TARGET, ""));
                    if (!remembers.isEmpty())
                        shulkerBullet.setTarget(remembers.get(0));
                }

                if (settings.getBool(RIDE, false)) {
                    entity.addPassenger(target);
                    if (entity instanceof Steerable steerable)
                        steerable.setSaddle(true);
                }

                Bukkit.getScheduler().runTaskLater(Fabled.inst(), entity::remove, 20L * (int) parseValues(caster, DURATION, level, 10));
            }
        }
        if (!entities.isEmpty()) {
            executeChildren(caster, level, entities, force);
        }
        return true;
    }

}
