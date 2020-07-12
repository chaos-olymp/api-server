package de.chaosolymp.apiserver;

import net.md_5.bungee.api.config.ServerInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class CachedServerPingHelper implements Runnable, Serializable {

    private final transient ApiServerPlugin plugin;
    private final List<String> onlineServers = new ArrayList<>();

    /**
     * Constructs the CachedServerPingHelper
     * @param plugin The plugin instance
     */
    CachedServerPingHelper(ApiServerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Map.Entry<String, ServerInfo> entry : this.plugin.getProxy().getServers().entrySet()) {
            entry.getValue().ping((result, error) -> {
                if(error == null) {
                    if(!onlineServers.contains(entry.getValue().getName())) {
                        onlineServers.add(entry.getValue().getName());
                        this.plugin.getLogger().info(String.format("%s is online.", entry.getValue().getName()));
                    }
                } else {
                    if(onlineServers.contains(entry.getValue().getName())) {
                        onlineServers.remove(entry.getValue().getName());
                        this.plugin.getLogger().info(String.format("%s is offline.", entry.getValue().getName()));
                    }
                }
            });
        }
    }

    /**
     * This method returns the server state of the last ping by the serverKey
     *
     * @param serverName The name of the server
     * @return Cached server state
     * @see ServerInfo#getName() The server name
     */
    public boolean isServerOnline(String serverName) {
        return this.onlineServers.contains(serverName);
    }
}
