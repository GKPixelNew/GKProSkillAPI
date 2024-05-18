package studio.magemonkey.fabled.dynamic.trigger;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerTeleportEvent;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;

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

    @Override
    public void setValues(PlayerTeleportEvent event, CastData data) {
        final double distance = event.getTo().distance(event.getFrom());
        data.put("api-distance", distance);
        data.put("api-from-x", event.getFrom().getX());
        data.put("api-from-y", event.getFrom().getY());
        data.put("api-from-z", event.getFrom().getZ());
        data.put("api-to-x", event.getTo().getX());
        data.put("api-to-y", event.getTo().getY());
        data.put("api-to-z", event.getTo().getZ());
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
