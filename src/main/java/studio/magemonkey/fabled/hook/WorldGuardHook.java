package studio.magemonkey.fabled.hook;

import com.google.common.collect.ImmutableList;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.Location;
import org.bukkit.World;
import studio.magemonkey.fabled.Fabled;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Fabled © 2024
 * studio.magemonkey.fabled.hook.WorldGuardHook
 */
public class WorldGuardHook {

    private static Method regionMethod;

    private static Class<?> vectorClass;
    private static Constructor<?> vectorConstructor;
    private static Method applicableRegionsMethod;

    /**
     * Fetches the list of region IDs applicable to a given location
     *
     * @param loc location to get region ids for
     * @return region IDs for the location
     */
    @SuppressWarnings("unchecked")
    public static List<String> getRegionIds(final Location loc) {
        try {
            final WorldGuardPlugin plugin = Fabled.getPlugin(WorldGuardPlugin.class);
            return (List<String>) getApplicableRegionsMethod().invoke((getRegionMethod().invoke(plugin,
                    loc.getWorld())), vectorConstructor.newInstance(loc.getX(), loc.getY(), loc.getZ()));
        } catch (final Exception e) {
            // Cannot handle world guard
            e.printStackTrace();
            return ImmutableList.of();
        }
    }

    private static Method getApplicableRegionsMethod() throws Exception {
        if (applicableRegionsMethod == null) {
            vectorClass = Class.forName("com.sk89q.worldedit.math.BlockVector3");
            vectorConstructor = vectorClass.getConstructor(double.class, double.class, double.class);
            applicableRegionsMethod = RegionManager.class.getMethod("getApplicableRegionsIDs", vectorClass);
        }
        return applicableRegionsMethod;
    }

    private static Method getRegionMethod() throws Exception {
        if (regionMethod == null) {
            regionMethod = WorldGuardPlugin.class.getDeclaredMethod("getRegionManager", World.class);
        }
        return regionMethod;
    }
}
