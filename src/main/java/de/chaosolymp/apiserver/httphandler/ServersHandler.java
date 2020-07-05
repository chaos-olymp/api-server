package de.chaosolymp.apiserver.httphandler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.chaosolymp.apiserver.ApiServerPlugin;

import java.io.IOException;
import java.io.OutputStream;

public final class ServersHandler implements HttpHandler {

    private final transient ApiServerPlugin plugin;
    private final transient Gson gson;

    public ServersHandler(final ApiServerPlugin plugin, final Gson gson) {
        this.plugin = plugin;
        this.gson = gson;
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        JsonArray array = new JsonArray();
        this.plugin.getProxy().getServers().forEach((key, value) -> {
            JsonObject object = new JsonObject();
            object.addProperty("internal-name", key);
            object.addProperty("name", value.getName());
            object.addProperty("motd", value.getMotd());
            object.addProperty("online", this.plugin.isServerOnline(value));
        });

        final String response = this.gson.toJson(array);
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
