package studio.magemonkey.fabled.task;

import studio.magemonkey.fabled.api.entity.BlockDisplayManager;
import studio.magemonkey.fabled.thread.IThreadTask;

/**
 * Repeating task that ticks all block display instances.
 */
public class BlockDisplayTask implements IThreadTask {
    @Override
    public void run() {
        BlockDisplayManager.tick();
    }
}
