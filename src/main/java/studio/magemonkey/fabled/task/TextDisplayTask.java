package studio.magemonkey.fabled.task;

import studio.magemonkey.fabled.api.entity.TextDisplayManager;
import studio.magemonkey.fabled.api.entity.VisibilityManager;
import studio.magemonkey.fabled.thread.RepeatThreadTask;

/**
 * Repeating task that ticks all text display instances.
 */
public class TextDisplayTask extends RepeatThreadTask {
    private int cleanupCounter = 0;

    public TextDisplayTask() {
        super(1, 1);
    }

    @Override
    public void run() {
        TextDisplayManager.tick();
        
        // Clean up stale visibility entries every 20 ticks (1 second)
        if (++cleanupCounter >= 20) {
            cleanupCounter = 0;
            VisibilityManager.cleanUp();
        }
    }
}
