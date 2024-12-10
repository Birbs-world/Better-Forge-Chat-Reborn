package com.rvt.bfcrmod.events;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import com.rvt.bfcrmod.BetterForgeChat;
import com.rvt.bfcrmod.MarkdownFormatter;
import com.rvt.bfcrmod.TextFormatter;
import com.rvt.bfcrmod.config.ConfigHandler;
import com.rvt.bfcrmod.config.IReloadable;
import com.rvt.bfcrmod.config.PermissionsHandler;
import com.rvt.bfcrmod.utils.BetterForgeChatUtilities;
import com.mojang.authlib.GameProfile;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ChatEventHandler implements IReloadable {
	private SimpleDateFormat timestampFormat = null;
	private boolean markdownEnabled = false;
	private String chatMessageFormat = "";
	private boolean loaded = false;
	
        @Override
	public void reloadConfigOptions() {
		loaded = false;
		timestampFormat = ConfigHandler.config.enableTimestamp.get() ? new SimpleDateFormat(ConfigHandler.config.timestampFormat.get()) : null;
		markdownEnabled = ConfigHandler.config.enableMarkdown.get();
		chatMessageFormat = ConfigHandler.config.chatMessageFormat.get();
		loaded = true;
	}
	
	public Style getHoverClickEventStyle(Component old) {
		if(old instanceof TranslatableContents tcmp) {
            Object[] args = tcmp.getArgs();
			for(Object arg : args) {
				if(arg instanceof MutableComponent tc) {
                    if(tc.getStyle() != null && tc.getStyle().getClickEvent() != null)
						return ((MutableComponent) arg).getStyle();
				}
			}
		}
		return null;
	}
	
	@SubscribeEvent
    public void onServerChat(ServerChatEvent e) {
		if(!loaded) return; // Just do nothing until everything's ready to go!
    	ServerPlayer player = e.getPlayer();
        GameProfile profile = player.getGameProfile();
    	UUID uuid = profile.getId();
		if(e == null || player == null) return;
        String msg = e.getMessage().getString();
		if(msg == null || (msg).isEmpty()) return;
		String tstamp = timestampFormat == null ? "" : timestampFormat.format(new Date());
		String name = BetterForgeChatUtilities.getRawPreferredPlayerName(profile);
		String fmat = chatMessageFormat.replace("$time", tstamp).replace("$name", name);
		MutableComponent beforeMsg = TextFormatter.stringToFormattedText(fmat.substring(0, fmat.indexOf("$msg")));
		MutableComponent afterMsg = TextFormatter.stringToFormattedText(fmat.substring(fmat.indexOf("$msg") + 4));
		boolean enableColor = PermissionsHandler.playerHasPermission(uuid, PermissionsHandler.coloredChatNode);
		boolean enableStyle = PermissionsHandler.playerHasPermission(uuid, PermissionsHandler.styledChatNode);
		
		// Create an error message if the player isn't allowed to use styles/colors
		String emsg = "";
		if(!enableColor && TextFormatter.messageContainsColorsOrStyles(msg, true))
			emsg = "You are not permitted to use colors";
		if(!enableStyle && TextFormatter.messageContainsColorsOrStyles(msg, false))
			emsg += !emsg.isEmpty() ? " or styles" : "You are not permitted to use styles";
		if(!emsg.isEmpty()) {
			MutableComponent ecmp = Component.literal(emsg + "!");
			ecmp.withStyle(ChatFormatting.BOLD);
			ecmp.withStyle(ChatFormatting.RED);
			player.sendSystemMessage(ecmp);
		}
		// Convert markdown to normal essentials formatting
		if(markdownEnabled && enableStyle && PermissionsHandler.playerHasPermission(uuid, PermissionsHandler.markdownChatNode))
			msg = MarkdownFormatter.markdownStringToFormattedString(msg);

		// Start generating the main TextComponent
		MutableComponent msgComp = TextFormatter.stringToFormattedText(msg, enableColor, enableStyle);

		// Append the hover and click event crap
		Style sty = getHoverClickEventStyle(e.getMessage());
		MutableComponent ecmp = Component.empty();
		if(sty != null && sty.getHoverEvent() != null)
			ecmp.setStyle(sty);
		e.setCanceled(true);
		
		MutableComponent newMessage = beforeMsg.append(msgComp.append(afterMsg));
		
		player.server.execute(() -> {
			BetterForgeChat.LOGGER.info("[CHAT] "+newMessage.getString());
			ServerMessageEvent.broadcastMessage(player.level(), newMessage);
		});
		
    }
}