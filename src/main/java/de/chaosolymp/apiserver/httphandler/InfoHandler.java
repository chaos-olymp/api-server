package de.chaosolymp.apiserver.httphandler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.chaosolymp.apiserver.ApiServerPlugin;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public final class InfoHandler implements HttpHandler {

    private final ApiServerPlugin plugin;
    private final Gson gson;

    public InfoHandler(ApiServerPlugin plugin, Gson gson) {
        this.plugin = plugin;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        final JsonObject object = new JsonObject();
        object.add("latest-version", this.getLatestSupportedVersion());
        object.addProperty("server-name", this.plugin.getProxy().getName());
        object.addProperty("server-version", this.plugin.getProxy().getVersion());
        object.addProperty("online-count", this.plugin.getProxy().getOnlineCount());
        object.addProperty("player-limit", this.plugin.getProxy().getConfig().getPlayerLimit());

        final String response = this.gson.toJson(object);
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private JsonObject getLatestSupportedVersion() {
        final JsonObject object = new JsonObject();
        object.addProperty("name", ProtocolConstants.SUPPORTED_VERSIONS.get(ProtocolConstants.SUPPORTED_VERSIONS.size() - 1));
        object.addProperty("id", ProtocolConstants.SUPPORTED_VERSION_IDS.get(ProtocolConstants.SUPPORTED_VERSION_IDS.size() - 1));

        return object;
    }
}
