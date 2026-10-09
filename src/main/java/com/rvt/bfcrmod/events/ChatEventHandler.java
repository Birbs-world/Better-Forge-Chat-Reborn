package com.rvt.bfcrmod.events;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import com.rvt.bfcrmod.BetterForgeChat;
import com.rvt.bfcrmod.Parser;
import com.rvt.bfcrmod.config.ConfigHandler;
import com.rvt.bfcrmod.config.IReloadable;
import com.rvt.bfcrmod.config.PermissionsHandler;
import com.rvt.bfcrmod.utils.BetterForgeChatUtilities;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = BetterForgeChat.MODID)
public class ChatEventHandler implements IReloadable {
	private static SimpleDateFormat timestampFormat = null;
	private static boolean markdownEnabled = false;
	private static String chatMessageFormat = "";
	private static ChatFormatting chatMessageColor = ChatFormatting.WHITE;
	private static boolean loaded = false;
	
	@Override
	public void reloadConfigOptions() {
		loaded = false;
		timestampFormat = ConfigHandler.config.enableTimestamp.get() ? new SimpleDateFormat(ConfigHandler.config.timestampFormat.get()) : null;
		markdownEnabled = ConfigHandler.config.enableMarkdown.get();
		chatMessageFormat = ConfigHandler.config.chatMessageFormat.get();
		chatMessageColor = ConfigHandler.config.chatMessageColor.get();
		loaded = true;
	}

	public static ChatFormatting getChatMessageColor() {
		BetterForgeChat.LOGGER.debug("Chat color from config: {}",chatMessageColor);
		return chatMessageColor;
	}

//	public static Style getHoverClickEventStyle(Component old) {
//		if(old instanceof TranslatableContents tcmp) {
//            Object[] args = tcmp.getArgs();
//			for(Object arg : args) {
//				if(arg instanceof MutableComponent tc) {
//                    if(tc.getStyle().getClickEvent() != null)
//						return tc.getStyle();
//				}
//			}
//		}
//		return null;
//	}
	
	@SubscribeEvent
    public static void onServerChat(ServerChatEvent e) {
		if(!loaded) return; // Just do nothing until everything's ready to go!
    	ServerPlayer player = e.getPlayer();
        UUID uuid = player.getUUID();
        String msg = e.getMessage().getString();
		if(msg.isEmpty()) return;
		String tstamp = timestampFormat == null ? "" : timestampFormat.format(new Date());
		String name = BetterForgeChatUtilities.getRawPreferredPlayerName(player);
		BetterForgeChat.LOGGER.debug("global message format: {}",chatMessageFormat);
		String fmat = chatMessageFormat.replace("$time", tstamp).replace("$name", name);
		BetterForgeChat.LOGGER.debug("formatted: {}",fmat);
		MutableComponent beforeMsg = Parser.parse(fmat.substring(0, fmat.indexOf("$msg")));
		BetterForgeChat.LOGGER.debug("before message: {}", beforeMsg);
		MutableComponent afterMsg = Parser.parse(fmat.substring(fmat.indexOf("$msg") + 4));
		BetterForgeChat.LOGGER.debug("after message: {}", afterMsg);
		boolean enableColor = PermissionsHandler.playerHasPermission(uuid, PermissionsHandler.coloredChatNode);
		boolean enableStyle = PermissionsHandler.playerHasPermission(uuid, PermissionsHandler.styledChatNode);
		
		// Create an error message if the player isn't allowed to use styles/colors
		String emsg = "";
		if(!enableColor && Parser.containsFormatting(msg, true))
			emsg = "You are not permitted to use colors";
		if(!enableStyle && Parser.containsFormatting(msg, false))
			emsg += !emsg.isEmpty() ? " or styles" : "You are not permitted to use styles";
		if(!emsg.isEmpty()) {
			MutableComponent ecmp = Component.literal(emsg + "!");
			ecmp.withStyle(ChatFormatting.BOLD);
			ecmp.withStyle(ChatFormatting.RED);
			player.sendSystemMessage(ecmp);
		}
		// Convert markdown to normal essentials formatting
		boolean parsedMarkdown = false;
		if(markdownEnabled && enableStyle && PermissionsHandler.playerHasPermission(uuid, PermissionsHandler.markdownChatNode)) {
			BetterForgeChat.LOGGER.debug("pre markdown formatted text: {}", msg);
			parsedMarkdown = true;
		}

		// Start generating the main TextComponent
		MutableComponent msgComp = parsedMarkdown
				? Parser.parse(msg)
				: Parser.parse(msg, enableColor, enableStyle);

		// Append the hover and click event crap
//		Style sty = getHoverClickEventStyle(e.getMessage());
//		MutableComponent ecmp = Component.empty();
//		if(sty != null && sty.getHoverEvent() != null)
//			Component.empty().setStyle(sty);
		e.setCanceled(true);
		
		MutableComponent newMessage = beforeMsg.append(msgComp.append(afterMsg));
		
		player.server.execute(() -> {
            BetterForgeChat.LOGGER.info("[CHAT] {}", newMessage.getString());
			broadcastMessage(player.serverLevel(), newMessage);
		});
		
    }
	
	private static void broadcastMessage(ServerLevel level, MutableComponent message) {
		MinecraftServer server = level.getServer();
        for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
            serverPlayer.sendSystemMessage(message);
        }
    }
}