package studio.magemonkey.fabled.dynamic.trigger;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;

import java.util.List;
import java.util.Locale;

/**
 * Trigger for when a potion effect is applied or removed from an entity.
 */
public class PotionEffectTrigger implements Trigger<EntityPotionEffectEvent> {

    @Override
    public String getKey() {
        return "POTION_EFFECT";
    }

    @Override
    public Class<EntityPotionEffectEvent> getEvent() {
        return EntityPotionEffectEvent.class;
    }

    @Override
    public boolean shouldTrigger(EntityPotionEffectEvent event, int level, Settings settings) {
        // Check action type (Start = ADDED/CHANGED, End = REMOVED/CLEARED)
        String type = settings.getString("type", "start").toLowerCase(Locale.ROOT);
        EntityPotionEffectEvent.Action action = event.getAction();
        
        boolean isStart = action == EntityPotionEffectEvent.Action.ADDED 
                       || action == EntityPotionEffectEvent.Action.CHANGED;
        boolean isEnd = action == EntityPotionEffectEvent.Action.REMOVED 
                     || action == EntityPotionEffectEvent.Action.CLEARED;
        
        if (type.equals("start") && !isStart) return false;
        if (type.equals("end") && !isEnd) return false;
        // "both" accepts either
        
        // Skip if the effect hasn't actually changed (same type, level, and duration)
        if (action == EntityPotionEffectEvent.Action.CHANGED) {
            PotionEffect oldEffect = event.getOldEffect();
            PotionEffect newEffect = event.getNewEffect();
            if (oldEffect != null && newEffect != null) {
                if (oldEffect.getType().equals(newEffect.getType())
                        && oldEffect.getAmplifier() == newEffect.getAmplifier()
                        && oldEffect.getDuration() == newEffect.getDuration()) {
                    return false;
                }
            }
        }
        
        // Check potion type filter
        List<String> potionTypes = settings.getStringList("potion");
        PotionEffectType modifiedType = event.getModifiedType();
        if (modifiedType == null) return false;
        
        String effectName = modifiedType.getKey().getKey().toUpperCase(Locale.ROOT).replace("_", " ");
        String effectNameAlt = modifiedType.getKey().getKey().toUpperCase(Locale.ROOT);
        
        if (!potionTypes.isEmpty() && !potionTypes.contains("Any")) {
            boolean matches = false;
            for (String potionType : potionTypes) {
                String upper = potionType.toUpperCase(Locale.ROOT).replace("_", " ");
                if (upper.equals(effectName) || upper.equals(effectNameAlt) || potionType.equalsIgnoreCase(effectName)) {
                    matches = true;
                    break;
                }
            }
            if (!matches) return false;
        }
        
        // Check level range (use the relevant effect based on action)
        PotionEffect relevantEffect = isStart ? event.getNewEffect() : event.getOldEffect();
        if (relevantEffect != null) {
            int effectLevel = relevantEffect.getAmplifier() + 1; // Amplifier is 0-indexed
            int minLevel = settings.getInt("min-level", 1);
            int maxLevel = settings.getInt("max-level", 999);
            
            if (effectLevel < minLevel || effectLevel > maxLevel) return false;
        }
        
        return true;
    }

    @Override
    public void setValues(EntityPotionEffectEvent event, CastData data) {
        PotionEffectType type = event.getModifiedType();
        if (type != null) {
            data.put("api-effect", type.getKey().getKey());
        }
        
        EntityPotionEffectEvent.Action action = event.getAction();
        boolean isStart = action == EntityPotionEffectEvent.Action.ADDED 
                       || action == EntityPotionEffectEvent.Action.CHANGED;
        
        PotionEffect effect = isStart ? event.getNewEffect() : event.getOldEffect();
        if (effect != null) {
            data.put("api-level", effect.getAmplifier() + 1);
            data.put("api-duration", effect.getDuration());
        } else {
            data.put("api-level", 0);
            data.put("api-duration", 0);
        }
        
        data.put("api-action", action.name().toLowerCase(Locale.ROOT));
    }

    @Override
    public LivingEntity getCaster(EntityPotionEffectEvent event) {
        if (event.getEntity() instanceof LivingEntity living) {
            return living;
        }
        return null;
    }

    @Override
    public LivingEntity getTarget(EntityPotionEffectEvent event, Settings settings) {
        if (event.getEntity() instanceof LivingEntity living) {
            return living;
        }
        return null;
    }
}
