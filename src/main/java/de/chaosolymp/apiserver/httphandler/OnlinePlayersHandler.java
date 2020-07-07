package de.chaosolymp.apiserver.httphandler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.chaosolymp.apiserver.ApiServerPlugin;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public final class OnlinePlayersHandler implements HttpHandler {

    private final transient ApiServerPlugin plugin;
    private final transient Gson gson;
    private final transient LuckPerms luckPerms;

    public OnlinePlayersHandler(final ApiServerPlugin plugin, final Gson gson) {
        this.plugin = plugin;
        this.gson = gson;
        this.luckPerms = LuckPermsProvider.get();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        JsonArray array = new JsonArray();

        this.plugin.getProxy().getPlayers().forEach(player -> array.add(this.getPlayerObject(player)));

        final String response = this.gson.toJson(array);

        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private JsonObject getPlayerObject(final ProxiedPlayer player) {
        final JsonObject object = new JsonObject();
        object.addProperty("name", player.getName());
        object.addProperty("uuid", player.getUniqueId().toString());
        object.addProperty("locale", player.getLocale().getISO3Language());
        object.addProperty("server", player.getServer().getInfo().getName());
        object.addProperty("ping", player.getPing());
        object.addProperty("group", Objects.requireNonNull(this.luckPerms.getUserManager().getUser(player.getUniqueId())).getPrimaryGroup());
        return object;
    }
}
