/**
 * SkillAPI
 * com.sucy.skill.listener.StatusListener
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2014 Steven Sucy
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
package com.sucy.skill.listener;

import com.sucy.skill.api.event.FlagApplyEvent;
import com.sucy.skill.api.event.PhysicalDamageEvent;
import com.sucy.skill.api.event.PlayerCastSkillEvent;
import com.sucy.skill.api.event.TrueDamageEvent;
import com.sucy.skill.api.util.FlagManager;
import com.sucy.skill.api.util.StatusFlag;
import mc.promcteam.engine.mccore.util.VersionManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Listener for applying default status flags for the API. You should
 * not use this class as it is already set up by the API.
 */
public class StatusListener extends SkillAPIListener {
    private static final HashMap<String, Long> messageTimers = new HashMap<String, Long>();

    private static final HashSet<String> interrupts = new HashSet<String>() {{
        add(StatusFlag.STUN);
        add(StatusFlag.SILENCE);
    }};

    private final Vector ZERO = new Vector(0, 0, 0);

    /**
     * Cleans up the listener data on shutdown
     */
    @Override
    public void cleanup() {
        messageTimers.clear();
    }

    /**
     * Clears data for players leaving the server
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        FlagManager.clearFlags(event.getPlayer());
    }

    /**
     * Cancels player movement when stunned or rooted
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent event) {
        if (((event.getPlayer()).isOnGround() || event.getTo().getY() > event.getFrom().getY()) &&
                check(event, event.getPlayer(), event.getPlayer(), false, StatusFlag.STUN, StatusFlag.ROOT, StatusFlag.CHANNELING)) {
            var loc = event.getTo();
            if (loc != null) {
                loc.setX(event.getFrom().getX());
                loc.setY(event.getFrom().getY());
                loc.setZ(event.getFrom().getZ());
                event.setTo(loc);
                event.getPlayer().setVelocity(ZERO);
            }
        }
    }

    /**
     * Applies interrupt effects, stopping channeling.
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInterrupt(FlagApplyEvent event) {
        if (interrupts.contains(event.getFlag()) && FlagManager.hasFlag(event.getEntity(), StatusFlag.CHANNELING)) {
            FlagManager.removeFlag(event.getEntity(), StatusFlag.CHANNELING);
            FlagManager.removeFlag(event.getEntity(), StatusFlag.CHANNEL);
        }
    }

    /**
     * Applies a slow potion to mobs when stunned/rooted due to
     * them not having a move event like the players.
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFlag(FlagApplyEvent event) {
        if (event.getFlag().equals(StatusFlag.STUN)
                || event.getFlag().equals(StatusFlag.ROOT)
                || event.getFlag().equals(StatusFlag.CHANNELING)) {
            if (!(event.getEntity() instanceof Player)) {
                event.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, event.getTicks(), 100));
            }
        }
    }

    /**
     * Cancels damage when an attacker is disarmed.
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.CUSTOM)
            return;

        LivingEntity damager = ListenerUtil.getDamager(event);
        check(event, damager, damager, true, StatusFlag.STUN, StatusFlag.DISARM);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPhysicalDamage(PhysicalDamageEvent event) {
        check(event, event.getDamager(), event.getDamager(), true, StatusFlag.CHANNEL);
    }

    /**
     * Cancels damage when a defender is invincible or inverting damage
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamaged(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.CUSTOM
                || !(event.getEntity() instanceof LivingEntity))
            return;

        checkAbsorbAndInvincible((LivingEntity) event.getEntity(), event, event.getDamage());
    }

    /**
     * Cancels damage when a defender is invincible or inverting damage
     *
     * @param event event details
     */
    public void onTrueDamage(TrueDamageEvent event) {
        checkAbsorbAndInvincible(event.getTarget(), event, event.getDamage());
    }

    /**
     * Shared code for true damage and regular damage events for absorb/invincible
     *
     * @param entity entity being hit
     * @param event  event details
     * @param damage damage amount
     */
    private void checkAbsorbAndInvincible(LivingEntity entity, Cancellable event, double damage) {
        if (check(event, entity, null, true, StatusFlag.ABSORB))
            VersionManager.heal(entity, damage);
        else
            check(event, entity, null, true, StatusFlag.INVINCIBLE);
    }

    /**
     * Cancels firing projectiles when the launcher is stunned or disarmed.
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof LivingEntity shooter) {
            check(event, shooter, shooter, true, StatusFlag.STUN, StatusFlag.DISARM, StatusFlag.CHANNELING);
        }
    }

    /**
     * Cancels players casting skills while stunned or silenced
     *
     * @param event event details
     */
    @EventHandler(ignoreCancelled = true)
    public void onCast(PlayerCastSkillEvent event) {
        check(event, event.getPlayer(), event.getPlayer(), true, StatusFlag.SILENCE, StatusFlag.STUN, StatusFlag.CHANNEL);
    }

    /**
     * Checks for the delay between sending status messages
     *
     * @param player player to check for
     * @return true if can send a message, false otherwise
     */
    private boolean checkTime(Player player) {
        if (!messageTimers.containsKey(player.getName())
                || System.currentTimeMillis() - messageTimers.get(player.getName()) > 1000) {
            messageTimers.put(player.getName(), System.currentTimeMillis());
            return true;
        }
        return false;
    }

    /**
     * Checks an entity for flags which cancel the event if applied.
     *
     * @param event    event that is cancelled if a flag is applied
     * @param entity   entity to check for flags
     * @param receiver entity to send messages to
     * @param flags    flags to check for
     * @return the canceled state of the event
     */
    private boolean check(Cancellable event, LivingEntity entity, LivingEntity receiver, boolean cancel, String... flags) {
        for (String flag : flags) {
            if (FlagManager.hasFlag(entity, flag)) {
                if (!event.isCancelled())
                    event.setCancelled(cancel);
                return true;
            }
        }
        return false;
    }
}
