package studio.magemonkey.fabled.dynamic.trigger;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerItemHeldEvent;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;

public class ItemHeldTrigger implements Trigger<PlayerItemHeldEvent> {
    @Override
    public String getKey() {
        return "ITEM HELD";
    }

    @Override
    public Class<PlayerItemHeldEvent> getEvent() {
        return PlayerItemHeldEvent.class;
    }

    @Override
    public boolean shouldTrigger(PlayerItemHeldEvent event, int level, Settings settings) {
        return true;
    }

    @Override
    public void setValues(PlayerItemHeldEvent event, CastData data) {
        data.put("api-old-slot", event.getPreviousSlot());
        data.put("api-new-slot", event.getNewSlot());
    }

    @Override
    public LivingEntity getCaster(PlayerItemHeldEvent event) {
        return event.getPlayer();
    }

    @Override
    public LivingEntity getTarget(PlayerItemHeldEvent event, Settings settings) {
        return event.getPlayer();
    }
}
