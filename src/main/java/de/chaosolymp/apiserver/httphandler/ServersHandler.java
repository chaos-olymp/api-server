package de.chaosolymp.apiserver.httphandler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.chaosolymp.apiserver.ApiServerPlugin;
import net.md_5.bungee.api.config.ServerInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public final class ServersHandler implements HttpHandler {

    private final transient ApiServerPlugin plugin;
    private final transient Gson gson;

    public ServersHandler(final ApiServerPlugin plugin, final Gson gson) {
        this.plugin = plugin;
        this.gson = gson;
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        JsonArray array = new JsonArray();
        for(Map.Entry<String, ServerInfo> entry : this.plugin.getProxy().getServers().entrySet()) {
            JsonObject object = new JsonObject();
            object.addProperty("internal-name", entry.getKey());
            object.addProperty("name", entry.getValue().getName());
            object.addProperty("motd", entry.getValue().getMotd());
            object.addProperty("online", this.plugin.isServerOnline(entry.getValue()));
            array.add(object);
        }

        final String response = this.gson.toJson(array);
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
