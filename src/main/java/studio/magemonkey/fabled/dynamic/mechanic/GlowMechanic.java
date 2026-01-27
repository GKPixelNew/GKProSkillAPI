package studio.magemonkey.fabled.dynamic.mechanic;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import studio.magemonkey.fabled.Fabled;

import java.util.Collections;
import java.util.List;

/**
 * Fabled © 2024
 * studio.magemonkey.fabled.dynamic.mechanic.GlowMechanic
 */
public class GlowMechanic extends MechanicComponent {
    private static final String DURATION = "duration";
    private static final byte GLOWING_FLAG = 0x40;

    @Override
    public String getKey() {
        return "glow";
    }

    @Override
    public boolean execute(final LivingEntity caster,
                           final int level,
                           final List<LivingEntity> targets,
                           boolean force) {
        if (caster instanceof Player player) {
            var duration = (int) parseValues(caster, DURATION, level, 5) * 20;
            for (var target : targets) {
                // Get current entity flags and add glowing
                byte flags = GLOWING_FLAG;
                if (target.getFireTicks() > 0) flags |= 0x01;
                if (target.isSneaking()) flags |= 0x02;
                if (target instanceof Player p && p.isSprinting()) flags |= 0x08;
                if (target.isSwimming()) flags |= 0x10;
                if (target.isInvisible()) flags |= 0x20;
                
                // Send metadata packet with glowing flag
                var metadata = new EntityData(0, EntityDataTypes.BYTE, flags);
                var packet = new WrapperPlayServerEntityMetadata(target.getEntityId(), Collections.singletonList(metadata));
                PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
                
                // Schedule removal of glow effect
                Bukkit.getScheduler().runTaskLater(Fabled.inst(), () -> {
                    // Remove glowing flag
                    byte resetFlags = 0;
                    if (target.getFireTicks() > 0) resetFlags |= 0x01;
                    if (target.isSneaking()) resetFlags |= 0x02;
                    if (target instanceof Player p && p.isSprinting()) resetFlags |= 0x08;
                    if (target.isSwimming()) resetFlags |= 0x10;
                    if (target.isInvisible()) resetFlags |= 0x20;
                    if (target.isGlowing()) resetFlags |= GLOWING_FLAG; // Keep if actually glowing
                    
                    var resetMetadata = new EntityData(0, EntityDataTypes.BYTE, resetFlags);
                    var resetPacket = new WrapperPlayServerEntityMetadata(target.getEntityId(), Collections.singletonList(resetMetadata));
                    PacketEvents.getAPI().getPlayerManager().sendPacket(player, resetPacket);
                }, duration);
            }
            return true;
        }
        return false;
    }
}
