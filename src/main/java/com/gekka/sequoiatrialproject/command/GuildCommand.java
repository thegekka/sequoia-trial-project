package com.gekka.sequoiatrialproject.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class GuildCommand {
    private static final String WYNNCRAFT_API = "https://api.wynncraft.com/v3";
    private static HttpClient client;

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext commandBuildContext) {
        dispatcher.register(
                ClientCommandManager.literal("guild")
                        .then(ClientCommandManager.argument("guild", StringArgumentType.string())
                                .executes(GuildCommand::execute))
        );
    }

    public static int execute(CommandContext<FabricClientCommandSource> context) {
        String guild = StringArgumentType.getString(context, "guild");
        client = HttpClient.newHttpClient();

        CompletableFuture<String> guildApiFuture = CompletableFuture.supplyAsync(() -> {
            String response = "";

            try {
                response = guildResponse(guild);
            } catch (IOException | InterruptedException e) {
                context.getSource().sendFeedback(Component.literal(e.toString()));
            }

            return response;
        });

        try {
            JSONObject json = new JSONObject(guildApiFuture.get());

            if (json.has("Error")) throw new Exception(json.getString("Error"));

            int members = json.getJSONObject("members").getInt("total");
            String name = json.getString("name");
            String prefix = json.getString("prefix");

            context.getSource().sendFeedback(Component.literal(name + " [" + prefix + "] has a total member of " + members + " members"));
        } catch (Exception e) {
            context.getSource().sendFeedback(Component.literal(e.toString()));
        }

        return 1;
    }

    public static String guildResponse(String guild) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(WYNNCRAFT_API + "/guild/" + guild))
                .header("identifier", "uuid")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}
