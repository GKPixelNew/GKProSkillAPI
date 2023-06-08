package com.sucy.skill.dynamic.trigger;

import com.sucy.skill.api.Settings;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Map;

/**
 * ProSkillAPI © 2023
 * com.sucy.skill.dynamic.trigger.BlockBreakTrigger
 */
public class TeleportTrigger implements Trigger<PlayerTeleportEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "TELEPORT";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<PlayerTeleportEvent> getEvent() {
        return PlayerTeleportEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(final PlayerTeleportEvent event, final int level, final Settings settings) {
        final String type = settings.getString("type", "any").replace(' ', '_').toUpperCase();
        return type.equalsIgnoreCase("ANY") || type.equalsIgnoreCase(event.getCause().name());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(final PlayerTeleportEvent event, final Map<String, Object> data) {
        final double distance = event.getTo().distance(event.getFrom());
        data.put("api-distance", distance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(final PlayerTeleportEvent event) {
        return event.getPlayer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(final PlayerTeleportEvent event, final Settings settings) {
        return event.getPlayer();
    }
}
