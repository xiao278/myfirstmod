package kx.myfirstmod.utils;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class TaskScheduler {
    private static final Map<UUID, ScheduledTask> scheduledTasks = new HashMap<>();

    public static void schedule(Runnable task, int ticks, UUID id) {
        scheduledTasks.put(id, new ScheduledTask(task, ticks));
    }

    public static void schedule(Runnable task, int ticks) {
        UUID taskId = UUID.randomUUID();
        schedule(task, ticks, taskId);
    }

    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            Iterator<Map.Entry<UUID, ScheduledTask>> iterator = scheduledTasks.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<UUID, ScheduledTask> entry = iterator.next();
                ScheduledTask scheduledTask = entry.getValue();
                scheduledTask.ticks--;
                if (scheduledTask.ticks <= 0) {
                    scheduledTask.task.run();
                    iterator.remove();
                }
            }
        });
    }

    private static class ScheduledTask {
        int ticks;
        Runnable task;

        ScheduledTask(Runnable task, int ticks) {
            this.ticks = ticks;
            this.task = task;
        }
    }
}
