package studio.magemonkey.fabled.task;

import studio.magemonkey.fabled.api.entity.BlockDisplayManager;
import studio.magemonkey.fabled.thread.RepeatThreadTask;

/**
 * Repeating task that ticks all block display instances.
 */
public class BlockDisplayTask extends RepeatThreadTask {
    public BlockDisplayTask() {
        super(1, 1);
    }

    @Override
    public void run() {
        BlockDisplayManager.tick();
    }
}
