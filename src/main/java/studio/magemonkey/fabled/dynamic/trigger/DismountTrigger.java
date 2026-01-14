package studio.magemonkey.fabled.dynamic.trigger;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDismountEvent;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;

/**
 * Triggers when an entity dismounts from another entity
 */
public class DismountTrigger implements Trigger<EntityDismountEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "DISMOUNT";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<EntityDismountEvent> getEvent() {
        return EntityDismountEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(final EntityDismountEvent event, final int level, final Settings settings) {
        return event.getEntity() instanceof LivingEntity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(final EntityDismountEvent event, final CastData data) {
        // No additional values to set
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(final EntityDismountEvent event) {
        return (LivingEntity) event.getEntity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(final EntityDismountEvent event, final Settings settings) {
        if (event.getDismounted() instanceof LivingEntity) {
            return (LivingEntity) event.getDismounted();
        }
        return (LivingEntity) event.getEntity();
    }
}
