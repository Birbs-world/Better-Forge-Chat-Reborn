package com.rvt.bfcrmod.utils;

import com.mojang.authlib.GameProfile;
import com.rvt.bfcrmod.BetterForgeChat;
import com.rvt.bfcrmod.TextFormatter;
import com.rvt.bfcrmod.config.ConfigHandler;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

public class BetterForgeChatUtilities {
	private static String playerNameFormat = "";
	
	public static void reloadConfig() {
		playerNameFormat = ConfigHandler.config.playerNameFormat.get();
		BetterForgeChat.LOGGER.debug("getting player name format: {}",playerNameFormat);
	}
	
	public static String getRawPreferredPlayerName(Player player) {
		BetterForgeChat.LOGGER.debug("raw preferredPlayername: {}",getRawPreferredPlayerName(player, true, true));
		return getRawPreferredPlayerName(player, true, true);
	}
	public static String getRawPreferredPlayerName(Player player, boolean enableNickname, boolean enableMetadata) {
		String name = BetterForgeChat.instance.nicknameProvider != null && enableNickname ? BetterForgeChat.instance.nicknameProvider.getPlayerChatName(player) : player.getName().getString();
		if(name == null) name = player.getName().getString(); /* No nickname (or null-nickname) provided */
		String pfx = "", sfx = "";
		if(enableMetadata && BetterForgeChat.instance.metadataProvider != null) {
			String[] dat = BetterForgeChat.instance.metadataProvider.getPlayerPrefixAndSuffix(player.getGameProfile());
			if(dat != null) {
				pfx = dat[0] != null ? dat[0] : "";
				sfx = dat[1] != null ? dat[1] : "";
			}
		}
		String fmat = playerNameFormat;
		if(fmat == null) {
			BetterForgeChat.LOGGER.warn("Could not get playerNameFormat from configuration file, please post issue on GitHub!");
			return player.getName().getString();
		} else{
			String playerNameFormatted = fmat.replace("$prefix", pfx).replace("$name", name).replace("$suffix", sfx);
			BetterForgeChat.LOGGER.debug("player name formatted to: {}",playerNameFormatted);
			return playerNameFormatted;
		}
	}
	public static MutableComponent getFormattedPlayerName(Player player) {
		return TextFormatter.stringToFormattedText(getRawPreferredPlayerName(player));
	}
	public static MutableComponent getFormattedPlayerName(Player player, boolean enableNickname, boolean enableMetadata) {
		return TextFormatter.stringToFormattedText(getRawPreferredPlayerName(player, enableNickname, enableMetadata));
	}
}
