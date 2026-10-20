package com.rvt.bfcrmod.events;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
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
import org.codehaus.plexus.util.StringUtils;

@EventBusSubscriber(modid = BetterForgeChat.MODID)
public class ChatEventHandler implements IReloadable {
	private static String timestampFormat = null;
	private static String timeZone = null;
	private static boolean enableTimestamp = false;
	private static String timeSeparator = null;
	private static boolean markdownEnabled = false;
	private static String chatMessageFormat = null;
	
    @Override
	public void reloadConfigOptions() {
		try {
			timestampFormat = ConfigHandler.config.timestampFormat.get();
			timeZone = ConfigHandler.config.setTimezone.get();
			enableTimestamp = ConfigHandler.config.enableTimestamp.get();
			timeSeparator = ConfigHandler.config.timeSeparator.get();
			markdownEnabled = ConfigHandler.config.enableMarkdown.get();
			chatMessageFormat = ConfigHandler.config.chatMessageFormat.get();
		} catch (Exception e) {
			BetterForgeChat.LOGGER.error("Failed to load config options: ", e);
		}
	}
	
	@SubscribeEvent
    public static void onServerChat(ServerChatEvent e) {
    	ServerPlayer player = e.getPlayer();
        UUID uuid = player.getUUID();
        String msg = e.getMessage().getString();
		if(msg.isEmpty()) return;
		String newMsg = chatMessageFormat;
		
		if (enableTimestamp && StringUtils.isNotBlank(timestampFormat) && StringUtils.isNotBlank(timeZone) && List.of(TimeZone.getAvailableIDs()).contains(timeZone)) {
			SimpleDateFormat dateFormat;
			dateFormat = new SimpleDateFormat(timestampFormat);
			dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
			String dateString = dateFormat.format(new Date());
			if (StringUtils.isNotBlank(timeSeparator)) dateString += timeSeparator;
			newMsg = newMsg.replace("$time", dateString);
		} else {
			newMsg = newMsg.replace("$time", "");
		}
		
		newMsg = newMsg.replace("$name", BetterForgeChatUtilities.getRawPreferredPlayerName(player).strip());
		newMsg = newMsg.replace("$msg", "&r" + msg);
		
		boolean enableColor = PermissionsHandler.playerHasPermission(uuid, PermissionsHandler.coloredChatNode);
		boolean enableStyle = PermissionsHandler.playerHasPermission(uuid, PermissionsHandler.styledChatNode);
		boolean enableMarkdown = markdownEnabled && enableStyle && PermissionsHandler.playerHasPermission(uuid, PermissionsHandler.markdownChatNode);
		
//		newMsg = newMsg.replaceAll(" +", " ");
		MutableComponent newComponent = Parser.parse(newMsg, enableColor, enableStyle, enableMarkdown);
		
		
		// Create an error message if the player isn't allowed to use styles/colors
		String emsg = "";
		if(!enableColor && Parser.containsFormatting(msg, true)) emsg = "You are not permitted to use colors";
		if(!enableStyle && Parser.containsFormatting(msg, false)) emsg += StringUtils.isNotBlank(emsg) ? " or styles" : "You are not permitted to use styles";
		if(StringUtils.isNotBlank(emsg)) {
			MutableComponent ecmp = Component.literal(emsg + "!").withStyle(ChatFormatting.RED);
			player.sendSystemMessage(ecmp);
		}
		e.setCanceled(true);
		
		player.server.execute(() -> {
            BetterForgeChat.LOGGER.info("[CHAT] {}", newComponent.getString());
			broadcastMessage(player.serverLevel(), newComponent);
		});
    }
	private static void broadcastMessage(ServerLevel level, MutableComponent message) {
		MinecraftServer server = level.getServer();
        for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
            serverPlayer.sendSystemMessage(message);
        }
    }
}