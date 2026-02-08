package studio.magemonkey.fabled.dynamic.mechanic.textdisplay;

import org.bukkit.entity.LivingEntity;
import studio.magemonkey.fabled.api.entity.TextDisplayManager;
import studio.magemonkey.fabled.dynamic.mechanic.MechanicComponent;

import java.util.List;

/**
 * Removes a text display spawned by the TextDisplayMechanic
 */
public class RemoveTextDisplayMechanic extends MechanicComponent {
    private static final String KEY = "key";

    @Override
    public String getKey() {
        return "remove text display";
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        String key = settings.getString(KEY, skill.getName());

        boolean removed = false;
        for (LivingEntity target : targets) {
            if (TextDisplayManager.remove(target, key)) {
                removed = true;
            }
        }
        return removed;
    }
}
