package de.chaosolymp.apiserver;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import de.chaosolymp.apiserver.httphandler.OnlinePlayersHandler;
import de.chaosolymp.apiserver.httphandler.ServersHandler;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class ApiServerPlugin extends Plugin {

    private final Map<String, Boolean> onlineServers = new HashMap<>();
    private final int port = 3010;

    @Override
    public void onEnable() {
        final long startTime = System.currentTimeMillis();
        try {
            this.startHttp();
        } catch (IOException e) {
            this.getLogger().severe(String.format("API Server warmup cancelled after %dms because of: %s", System.currentTimeMillis() - startTime, e.getLocalizedMessage()));
            return;
        }

        this.getProxy().getScheduler().schedule(this, () -> this.getProxy().getServers().forEach((key, value) -> value.ping((response, error) -> onlineServers.put(value.getName(), error != null))), 0, 2, TimeUnit.MINUTES);
        this.getLogger().info(String.format("HTTP Server listening on port :%d", this.port));
        this.getLogger().info(String.format("API Server warmup finished (Took %dms)", System.currentTimeMillis() - startTime));
    }

    private void startHttp() throws IOException {
        final HttpServer server = HttpServer.create(new InetSocketAddress(this.port), 0);
        final Gson gson = new Gson();
        server.createContext("/players", new OnlinePlayersHandler(this, gson));
        server.createContext("/servers", new ServersHandler(this, gson));
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    public boolean isServerOnline(ServerInfo info) {
        return this.onlineServers.getOrDefault(info.getName(), false);
    }
}
