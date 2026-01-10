package studio.magemonkey.fabled.hook;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;

import java.util.List;

/**
 * Fabled © 2024
 * studio.magemonkey.fabled.hook.WorldGuardHook
 */
public class WorldGuardHook {
    /**
     * Fetches the list of region IDs applicable to a given location
     *
     * @param loc location to get region ids for
     * @return region IDs for the location
     */
    public static List<String> getRegionIds(final Location loc) {
        RegionManager rm = WorldGuard.getInstance().getPlatform().getRegionContainer()
                .get(BukkitAdapter.adapt(loc.getWorld()));
        if (rm == null) return List.of();
        return rm.getApplicableRegions(BlockVector3.at(loc.getX(), loc.getY(), loc.getZ()))
                .getRegions().stream().map(ProtectedRegion::getId).toList();
    }
}
