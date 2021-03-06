package de.chaosolymp.apiserver;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import de.chaosolymp.apiserver.httphandler.InfoHandler;
import de.chaosolymp.apiserver.httphandler.OnlinePlayersHandler;
import de.chaosolymp.apiserver.httphandler.ServersHandler;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public final class ApiServerPlugin extends Plugin {

    private CachedServerPingHelper cachedServerPingHelper;
    private final static int PORT = 3010;

    private transient ScheduledTask task;
    private transient HttpServer server;

    @Override
    public void onEnable() {
        final long startTime = System.currentTimeMillis();
        try {
            this.startHttp();
        } catch (IOException e) {
            this.getLogger().severe(String.format("API Server warmup cancelled after %dms because of: %s", System.currentTimeMillis() - startTime, e.getLocalizedMessage()));
            return;
        }

        this.cachedServerPingHelper = new CachedServerPingHelper(this);
        this.task = this.getProxy().getScheduler().schedule(this, this.cachedServerPingHelper, 0, 30, TimeUnit.SECONDS);
        this.getLogger().info(String.format("HTTP Server listening on port :%d", ApiServerPlugin.PORT));
        this.getLogger().info(String.format("API Server warmup finished (Took %dms)", System.currentTimeMillis() - startTime));
    }

    @Override
    public void onDisable() {
        this.server.stop(0);
        this.task.cancel();
    }

    private void startHttp() throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(ApiServerPlugin.PORT), 0);
        final Gson gson = new Gson();
        server.createContext("/info", new InfoHandler(this, gson));
        server.createContext("/players", new OnlinePlayersHandler(this, gson));
        server.createContext("/servers", new ServersHandler(this, gson));
        server.setExecutor(new ScheduledExecutor(this));
        server.start();
    }

    public CachedServerPingHelper getServerPingHandler() {
        return this.cachedServerPingHelper;
    }
}
