package studio.magemonkey.fabled.dynamic.trigger;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityMountEvent;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;

/**
 * Triggers when an entity mounts another entity
 */
public class MountTrigger implements Trigger<EntityMountEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "MOUNT";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<EntityMountEvent> getEvent() {
        return EntityMountEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(final EntityMountEvent event, final int level, final Settings settings) {
        return event.getEntity() instanceof LivingEntity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(final EntityMountEvent event, final CastData data) {
        // No additional values to set
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(final EntityMountEvent event) {
        return (LivingEntity) event.getEntity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(final EntityMountEvent event, final Settings settings) {
        if (event.getMount() instanceof LivingEntity) {
            return (LivingEntity) event.getMount();
        }
        return (LivingEntity) event.getEntity();
    }
}
