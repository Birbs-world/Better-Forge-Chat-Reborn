package com.jeremiahbl.bfcrmod.utils;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.jeremiahbl.bfcrmod.BetterForgeChat;
import com.jeremiahbl.bfcrmod.config.PlayerData;
import com.mojang.authlib.GameProfile;

public class IntegratedNicknameProvider implements INicknameProvider {
	@Override public String getPlayerNickname(@NonNull GameProfile player) {
		String nick = PlayerData.getNickname(player.getId());
		BetterForgeChat.LOGGER.info("DEV - retrieved nickname: {} for {}",nick,player.getName());
		return nick;
	}
	@Override public @NonNull String getProviderName() {
		return "BetterForgeChat";
	}
}
