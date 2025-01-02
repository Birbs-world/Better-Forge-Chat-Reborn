package com.rvt.bfcrmod.utils;

import com.mojang.authlib.GameProfile;
import com.rvt.bfcrmod.BetterForgeChat;
import com.rvt.bfcrmod.TextFormatter;
import com.rvt.bfcrmod.config.ConfigHandler;

import net.minecraft.network.chat.MutableComponent;

public class BetterForgeChatUtilities {
	private static String playerNameFormat = "";
	
	public static void reloadConfig() {
		playerNameFormat = ConfigHandler.config.playerNameFormat.get();
		BetterForgeChat.LOGGER.debug("getting player name format: {}",playerNameFormat);
	}
	
	public static String getRawPreferredPlayerName(GameProfile player) {
		BetterForgeChat.LOGGER.debug("raw preferredPlayername: {}",getRawPreferredPlayerName(player, true, true));
		return getRawPreferredPlayerName(player, true, true);
	}
	public static String getRawPreferredPlayerName(GameProfile player, boolean enableNickname, boolean enableMetadata) {
		String name = BetterForgeChat.instance.nicknameProvider != null && enableNickname ? BetterForgeChat.instance.nicknameProvider.getPlayerChatName(player) : player.getName();
		if(name == null) name = player.getName(); /* No nickname (or null-nickname) provided */
		String pfx = "", sfx = "";
		if(enableMetadata && BetterForgeChat.instance.metadataProvider != null) {
			String[] dat = BetterForgeChat.instance.metadataProvider.getPlayerPrefixAndSuffix(player);
			if(dat != null) {
				pfx = dat[0] != null ? dat[0] : "";
				sfx = dat[1] != null ? dat[1] : "";
			}
		}
		String fmat = playerNameFormat;
		if(fmat == null) {
			BetterForgeChat.LOGGER.warn("Could not get playerNameFormat from configuration file, please post issue on GitHub!");
			return player.getName();
		} else{
			String playerNameFormatted = fmat.replace("$prefix", pfx).replace("$name", name).replace("$suffix", sfx);
			BetterForgeChat.LOGGER.debug("player name formatted to: {}",playerNameFormatted);
			return playerNameFormatted;
		}
	}
	public static MutableComponent getFormattedPlayerName(GameProfile player) {
		return TextFormatter.stringToFormattedText(getRawPreferredPlayerName(player));
	}
	public static MutableComponent getFormattedPlayerName(GameProfile player, boolean enableNickname, boolean enableMetadata) {
		return TextFormatter.stringToFormattedText(getRawPreferredPlayerName(player, enableNickname, enableMetadata));
	}
}
