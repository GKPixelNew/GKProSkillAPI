/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.mechanic.AccurateLaunchMechanic
 * <p>
 * The MIT License (MIT)
 */
package studio.magemonkey.fabled.dynamic.mechanic;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.dynamic.ComponentType;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Launches targets toward a computed destination using a ballistic-style
 * trajectory.
 */
public class AccurateLaunchMechanic extends MechanicComponent {

    private static final String RELATIVE = "relative";     // caster-looking | target-looking | caster-to-target | target-to-caster (legacy: caster, target, between)
    private static final String RESET_Y = "reset-y";
    private static final String FORWARD = "forward";
    private static final String UPWARD = "upward";
    private static final String RIGHT = "right";
    private static final String SPEED = "speed";
    private static final String MAX_VELOCITY = "max-velocity";
    private static final String MIN_TICKS = "min-ticks";
    private static final String FAR_THRESHOLD = "far-threshold";

    private static final double DEFAULT_SPEED = 2.0;
    private static final double DEFAULT_MAX_VELOCITY = 4.0;
    private static final int DEFAULT_MIN_TICKS = 5;
    private static final double DEFAULT_FAR_THRESHOLD = 4.0;

    // Map to handle extended high-power launches
    private static final HashMap<UUID, ExtraLaunch> launching = new HashMap<>();

    @Override
    public String getKey() {
        return "accurate launch";
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
        double  farThreshold = parseValues(caster, FAR_THRESHOLD, level, DEFAULT_FAR_THRESHOLD);

        String relative = settings.getString(RELATIVE, "target-looking").toLowerCase();

        for (LivingEntity target : targets) {
            // 1. Cross-world check
            if (caster.getWorld() != target.getWorld()) {
                continue;
            }

            LivingEntity launchSubject = target;
            if (relative.startsWith("caster")) {
                launchSubject = caster;
            }

            // Cancel existing launch task if any
            if (launching.containsKey(launchSubject.getUniqueId())) {
                ExtraLaunch existing = launching.remove(launchSubject.getUniqueId());
                Bukkit.getScheduler().cancelTask(existing.schedId);
            }

            Vector rawDir = getDirection(caster, target, relative);

            if (rawDir == null || rawDir.lengthSquared() == 0) {
                continue;
            }

            Vector dir = rawDir.clone();
            if (resetY) {
                dir.setY(0);
            }
            dir.normalize();

            // 4. Vertical Fix
            Vector up = new Vector(0, 1, 0);
            Vector rightVec;
            if (Math.abs(dir.getY()) > 0.99) {
                if (relative.contains("looking") || relative.equals("caster") || relative.equals("target")) {
                    LivingEntity dirSource = relative.contains("target") ? target : caster;
                    Location loc = dirSource.getLocation();
                    loc.setPitch(0);
                    rightVec = loc.getDirection().crossProduct(up);
                } else {
                    rightVec = new Vector(1, 0, 0);
                }
            } else {
                rightVec = dir.clone().crossProduct(up);
            }

            // 3. Speed as Power for ALL Modes
            // We use the calculated direction and apply the speed directly as velocity.
            // This ensures that high speed always results in high velocity, regardless of distance.
            Vector launchDir = dir.clone().multiply(forward).add(rightVec.multiply(right)).add(new Vector(0, upward, 0));
            if (launchDir.lengthSquared() < 1e-5) {
                launchDir = dir.clone();
            }
            
            Vector velocity = launchDir.normalize().multiply(speed);

            // Check for Far Launch
            if (velocity.length() > farThreshold) {
                ExtraLaunch launchData = new ExtraLaunch();
                launchData.entity = launchSubject;
                launchData.speed = speed;

                // Homing setup
                if (relative.equals("caster-to-target")) {
                    launchData.targetEntity = target;
                } else if (relative.equals("target-to-caster")) {
                    launchData.targetEntity = caster;
                }

                launchData.times = Math.max(1, (int)((velocity.length() - farThreshold) * 5.0));
                Vector safeVelocity = velocity.clone().normalize().multiply(farThreshold);
                launchData.vector = safeVelocity;

                startLaunch(launchData);
            } else {
                launchSubject.setVelocity(velocity);
            }

            // 2. First Only for Caster Launch
            if (launchSubject == caster) {
                break;
            }
        }
        return true;
    }

    private Vector getDirection(LivingEntity caster, LivingEntity target, String relative) {
        return switch (relative) {
            case "caster-looking", "caster" -> caster.getLocation().getDirection();
            case "target-looking", "target" -> target.getLocation().getDirection();
            case "caster-to-target" -> target.getLocation().toVector().subtract(caster.getLocation().toVector());
            case "target-to-caster", "between" -> caster.getLocation().toVector().subtract(target.getLocation().toVector());
            default -> target.getLocation().getDirection();
        };
    }

    /**
     * Ballistic-style trajectory inspired by CmdLaunch: gravity and drag
     * approximations.
     */
    private Vector calculateBallisticVelocity(Vector start, Vector end, double speed, int minTicks) {
        double g = 0.08;
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

    private void startLaunch(ExtraLaunch launchData) {
        launching.put(launchData.entity.getUniqueId(), launchData);
        launchData.schedId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Fabled.inst(), new LaunchRunnable(launchData), 0L, 2L);
    }

    private static class ExtraLaunch {
        LivingEntity entity;
        LivingEntity targetEntity;
        Vector vector;
        int times;
        int schedId;
        Location prevLoc;
        double speed;
        Location stuckCheckLoc;
        int stuckCheckTicks;
    }

    private class LaunchRunnable implements Runnable {
        private final ExtraLaunch launchData;

        public LaunchRunnable(ExtraLaunch data) {
            this.launchData = data;
        }

        @Override
        public void run() {
            LivingEntity entity = launchData.entity;
            LivingEntity target = launchData.targetEntity;

            if (entity == null || !entity.isValid()) {
                cancel();
                return;
            }

            // Stuck Check
            launchData.stuckCheckTicks++;
            if (launchData.stuckCheckTicks >= 3) {
                if (launchData.stuckCheckLoc != null) {
                    if (entity.getLocation().distance(launchData.stuckCheckLoc) <= 5) {
                        cancel();
                        return;
                    }
                }
                launchData.stuckCheckLoc = entity.getLocation();
                launchData.stuckCheckTicks = 0;
            }

            // Homing Logic
            if (target != null && target.isValid()) {
                double dist = entity.getLocation().distance(target.getLocation());
                if (dist > 25.0) { // Close enough distance
                    // Update vector to point to target
                    Vector dir = target.getLocation().add(0, 1.5, 0).toVector().subtract(entity.getLocation().toVector()).normalize().multiply(launchData.speed);
                    launchData.vector = dir;
                    
                    // Extend flight
                    launchData.times = 20; 
                } else {
                    // Close enough, stop homing and apply final exact trajectory
                    launchData.targetEntity = null;
                    
                    // Calculate exact trajectory for the final approach
                    Vector finalVelocity = calculateBallisticVelocity(entity.getLocation().toVector(), target.getLocation().add(0, 1.5, 0).toVector(), launchData.speed, 5);
                    if (finalVelocity != null) {
                        entity.setVelocity(finalVelocity);
                    }
                    
                    cancel();
                    return;
                }
            }

            // 1. Simulate Drag/Gravity
            launchData.vector.setY(launchData.vector.getY() - 0.075);

            boolean isRegistered = launching.containsKey(entity.getUniqueId());
            boolean hasTimeLeft = launchData.times > 0;

            if (isRegistered && hasTimeLeft) {
                boolean isFirstTick = launchData.prevLoc == null;
                boolean isInAir = !entity.isOnGround();
                boolean isFlying = entity instanceof Player && ((Player) entity).isFlying();
                boolean isGliding = entity instanceof Player && ((Player) entity).isGliding();

                boolean highVerticalVelocity = launchData.vector.getY() > 1.0;
                boolean hasNotMoved = launchData.prevLoc != null && launchData.prevLoc.distance(entity.getLocation()) < 0.1;
                boolean hitCeiling = highVerticalVelocity && hasNotMoved;

                if (isFirstTick || (isInAir && !isFlying && !isGliding && !hitCeiling)) {
                    launchData.prevLoc = entity.getLocation();
                    entity.setVelocity(launchData.vector);
                    launchData.times--;
                    return;
                }
            }

            cancel();
        }

        private void cancel() {
            Bukkit.getScheduler().cancelTask(launchData.schedId);
            launching.remove(launchData.entity.getUniqueId());
        }
    }

    private double component(double start, double end, int t, double gravity, double drag) {
        double displacement = end - start;
        double a = (1 - Math.pow(drag, t)) / (1 - drag);
        double term1 = displacement / a;
        double term2 = gravity != 0 ? (gravity * drag / (1 - drag)) * (t / a - 1) : 0;
        return term1 + term2;
    }

}
