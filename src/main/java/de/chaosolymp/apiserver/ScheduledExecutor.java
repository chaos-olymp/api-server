package de.chaosolymp.apiserver;

import java.util.concurrent.Executor;

public final class ScheduledExecutor implements Executor {

    private final transient ApiServerPlugin plugin;

    public ScheduledExecutor(final ApiServerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Runnable command) {
        this.plugin.getProxy().getScheduler().runAsync(this.plugin, command);
    }
}
