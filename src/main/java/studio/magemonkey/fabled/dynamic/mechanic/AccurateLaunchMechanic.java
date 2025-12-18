/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.mechanic.AccurateLaunchMechanic
 * <p>
 * The MIT License (MIT)
 */
package studio.magemonkey.fabled.dynamic.mechanic;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import studio.magemonkey.fabled.dynamic.ComponentType;

import java.util.List;

/**
 * Launches targets toward a computed destination using a ballistic-style trajectory.
 */
public class AccurateLaunchMechanic extends MechanicComponent {

    private static final String RELATIVE     = "relative";     // caster | target | between
    private static final String RESET_Y      = "reset-y";
    private static final String FORWARD      = "forward";
    private static final String UPWARD       = "upward";
    private static final String RIGHT        = "right";
    private static final String SPEED        = "speed";
    private static final String MAX_VELOCITY = "max-velocity";
    private static final String MIN_TICKS    = "min-ticks";

    private static final double DEFAULT_SPEED        = 2.0;
    private static final double DEFAULT_MAX_VELOCITY = 4.0;
    private static final int    DEFAULT_MIN_TICKS    = 5;

    @Override
    public String getKey() {
        return "accurate-launch";
    }

    @Override
    public ComponentType getType() {
        return ComponentType.MECHANIC;
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        if (targets.isEmpty()) return false;

        boolean resetY      = settings.getBool(RESET_Y, true);
        double  forward     = parseValues(caster, FORWARD, level, 0);
        double  upward      = parseValues(caster, UPWARD, level, 0);
        double  right       = parseValues(caster, RIGHT, level, 0);
        double  speed       = parseValues(caster, SPEED, level, DEFAULT_SPEED);
        double  maxVelocity = parseValues(caster, MAX_VELOCITY, level, DEFAULT_MAX_VELOCITY);
        int     minTicks    = (int) Math.max(1, parseValues(caster, MIN_TICKS, level, DEFAULT_MIN_TICKS));

        String relative = settings.getString(RELATIVE, "target").toLowerCase();

        for (LivingEntity target : targets) {
            Vector dir = switch (relative) {
                case "caster" -> caster.getLocation().getDirection();
                case "between" -> target.getLocation().toVector().subtract(caster.getLocation().toVector());
                default -> target.getLocation().getDirection(); // "target"
            };

            if (resetY) dir.setY(0);
            dir.normalize();
            Vector up = new Vector(0, 1, 0);
            Vector rightVec = dir.clone().crossProduct(up);

            Vector offset = dir.multiply(forward).add(rightVec.multiply(right)).add(new Vector(0, upward, 0));
            Location origin = target.getLocation();
            Location destination = origin.clone().add(offset);

            Vector velocity = calculateBallisticVelocity(origin.toVector(), destination.toVector(), speed, minTicks);
            if (velocity == null || velocity.lengthSquared() == 0) {
                continue;
            }

            if (velocity.length() > maxVelocity) {
                velocity.normalize().multiply(maxVelocity);
            }

            target.setVelocity(velocity);
        }
        return true;
    }

    /**
     * Ballistic-style trajectory inspired by CmdLaunch: gravity and drag approximations.
     */
    private Vector calculateBallisticVelocity(Vector start, Vector end, double speed, int minTicks) {
        double g     = 0.08;
        double dragH = 0.91;
        double dragV = 0.98;

        double dist = start.distance(end);
        double effectiveSpeed = Math.max(0.5, speed);
        int t = Math.max(minTicks, (int) (dist / effectiveSpeed));

        double vX = component(start.getX(), end.getX(), t, 0, dragH);
        double vZ = component(start.getZ(), end.getZ(), t, 0, dragH);
        double vY = component(start.getY(), end.getY(), t, g, dragV);

        if (Double.isNaN(vX) || Double.isNaN(vY) || Double.isNaN(vZ)) {
            return null;
        }
        return new Vector(vX, vY, vZ);
    }

    private double component(double start, double end, int t, double gravity, double drag) {
        double displacement = end - start;
        double a = (1 - Math.pow(drag, t)) / (1 - drag);
        double term1 = displacement / a;
        double term2 = gravity != 0 ? (gravity * drag / (1 - drag)) * (t / a - 1) : 0;
        return term1 + term2;
    }
}
