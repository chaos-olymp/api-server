package de.chaosolymp.apiserver;

import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.HashMap;
import java.util.Map;

public final class ServerPingHandler implements Runnable {

    private final transient ApiServerPlugin plugin;
    public final Map<String, Boolean> onlineServers = new HashMap<>();

    public ServerPingHandler(ApiServerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for(Map.Entry<String, ServerInfo> entry : this.plugin.getProxy().getServers().entrySet()) {
            entry.getValue().ping((result, error) -> {
                if(error != null) {
                    onlineServers.put(entry.getKey(), true);
                } else {
                    onlineServers.put(entry.getKey(), false);
                }
            });
        }
    }
}
