package com.jeremiahbl.bfcrmod.events;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ServerMessageEvent {
    public static void sendMessage(Player player, MutableComponent message) {
        sendMessage(player, message, false);
    }
    public static void sendMessage(Player player, MutableComponent message, boolean emptyline) {
        if (emptyline) {
            player.sendSystemMessage(Component.literal(""));
        }
        player.sendSystemMessage(message);
    }
    public static void broadcastMessage(Level world, MutableComponent message) {
        MinecraftServer server = world.getServer();
        if (server != null) {

            for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
                sendMessage((Player) serverPlayer, message);
            }

        }
    }
}