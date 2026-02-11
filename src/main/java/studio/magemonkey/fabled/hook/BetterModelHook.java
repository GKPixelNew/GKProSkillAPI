package studio.magemonkey.fabled.hook;

import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.animation.AnimationIterator;
import kr.toxicity.model.api.animation.AnimationModifier;
import kr.toxicity.model.api.bukkit.platform.BukkitAdapter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@Slf4j
public class BetterModelHook {
    public static void track(Entity entity, String modelId) {
        var model = BetterModel.model(modelId);
        if (model.isEmpty()) {
            log.error("No model found for modelId {}", modelId);
            return;
        }
        model.get().getOrCreate(BukkitAdapter.adapt(entity));
    }

    public static void animateLimb(Player player, String limbId, String animationId,
                                   int lerpIn, int lerpOut, float speed, String type) {
        var limb = BetterModel.limb(limbId);
        if (limb.isEmpty()) {
            log.error("No limb found for limbId {}", limbId);
            return;
        }
        var tracker = limb.get().getOrCreate(BukkitAdapter.adapt(player));
        tracker.animate(animationId, AnimationModifier.builder()
                .start(lerpIn).end(lerpOut).speed(speed).type(AnimationIterator.Type.valueOf(type))
                .build(), tracker::close);
    }
}
