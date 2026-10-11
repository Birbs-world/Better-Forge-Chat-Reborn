package com.rvt.bfcrmod.utils;

import com.rvt.bfcrmod.BetterForgeChat;
import com.rvt.bfcrmod.Parser;
import com.rvt.bfcrmod.config.ConfigHandler;

import com.rvt.bfcrmod.config.IReloadable;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import org.codehaus.plexus.util.StringUtils;

public class BetterForgeChatUtilities implements IReloadable {
	private static String playerNameFormat = "";
	@Override
	public void reloadConfigOptions() {
		playerNameFormat = ConfigHandler.config.playerNameFormat.get();
	}
	
	public static String getRawPreferredPlayerName(Player player) {
		BetterForgeChat.LOGGER.debug("raw preferredPlayername: {}",getRawPreferredPlayerName(player, true, true));
		return getRawPreferredPlayerName(player, true, true);
	}
	public static String getRawPreferredPlayerName(Player player, boolean enableNickname, boolean enableMetadata) {
		String name = BetterForgeChat.instance.nicknameProvider != null && enableNickname ? BetterForgeChat.instance.nicknameProvider.getPlayerChatName(player) : player.getName().getString();
        String pfx = "", sfx = "";
		if(enableMetadata && BetterForgeChat.instance.metadataProvider != null) {
			String[] dat = BetterForgeChat.instance.metadataProvider.getPlayerPrefixAndSuffix(player.getGameProfile());
			if(dat != null) {
				pfx = dat[0] != null ? dat[0] : "";
				sfx = dat[1] != null ? dat[1] : "";
			}
		}
		String fmat = playerNameFormat;
		if(StringUtils.isBlank(fmat)) {
			BetterForgeChat.LOGGER.warn("Could not get playerNameFormat from configuration file, please post issue on GitHub!");
			return player.getName().getString();
		} else {
			String playerNameFormatted = fmat.replace("$prefix", pfx.strip()).replace("$name", name).replace("$suffix", sfx.strip());
			BetterForgeChat.LOGGER.debug("player name formatted to: {}",playerNameFormatted);
			return playerNameFormatted;
		}
	}
	public static MutableComponent getFormattedPlayerName(Player player) {
		return Parser.parse(getRawPreferredPlayerName(player));
	}
	public static MutableComponent getFormattedPlayerName(Player player, boolean enableNickname, boolean enableMetadata) {
		return Parser.parse(getRawPreferredPlayerName(player, enableNickname, enableMetadata));
	}
}
