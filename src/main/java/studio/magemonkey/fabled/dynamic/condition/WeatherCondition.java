package studio.magemonkey.fabled.dynamic.condition;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

/**
 * Fabled © 2024
 * studio.magemonkey.fabled.dynamic.condition.Weather
 */
public class WeatherCondition extends ConditionComponent {
    private String TYPE = "type";

    @Override
    public boolean test(final LivingEntity caster, final int level, final LivingEntity target) {
        final String   type        = settings.getString(TYPE).toLowerCase();
        final World    world       = target.getWorld();
        final Location loc         = target.getLocation();
        final double   temperature = loc.getBlock().getTemperature();

        switch (type) {
            case "thunder":
                return world.isThundering() && temperature <= 1;
            case "rain":
                return world.hasStorm() && temperature > 0.15 && temperature <= 1;
            case "snow":
                return world.hasStorm() && temperature <= 0.15;
            default:
                return !world.hasStorm() || temperature > 1;
        }
    }

    @Override
    public String getKey() {
        return "weather";
    }
}
