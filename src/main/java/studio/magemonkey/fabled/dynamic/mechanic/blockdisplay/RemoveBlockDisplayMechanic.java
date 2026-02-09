package studio.magemonkey.fabled.dynamic.mechanic.blockdisplay;

import org.bukkit.entity.LivingEntity;
import studio.magemonkey.fabled.api.entity.BlockDisplayManager;
import studio.magemonkey.fabled.dynamic.mechanic.MechanicComponent;

import java.util.List;

/**
 * Removes a block display spawned by the BlockDisplayMechanic
 */
public class RemoveBlockDisplayMechanic extends MechanicComponent {
    private static final String KEY = "key";

    @Override
    public String getKey() {
        return "remove block display";
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        String key = settings.getString(KEY, skill.getName());

        boolean removed = false;
        for (LivingEntity target : targets) {
            if (BlockDisplayManager.remove(target, key)) {
                removed = true;
            }
        }
        return removed;
    }
}
