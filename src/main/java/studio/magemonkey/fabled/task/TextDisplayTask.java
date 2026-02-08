package studio.magemonkey.fabled.task;

import studio.magemonkey.fabled.api.entity.TextDisplayManager;
import studio.magemonkey.fabled.thread.RepeatThreadTask;

/**
 * Repeating task that ticks all text display instances.
 */
public class TextDisplayTask extends RepeatThreadTask {

    public TextDisplayTask() {
        super(1, 1);
    }

    @Override
    public void run() {
        TextDisplayManager.tick();
    }
}
