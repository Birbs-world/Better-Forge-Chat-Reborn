package com.rvt.bfcrmod.utils;

import net.minecraft.world.entity.player.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import com.mojang.authlib.GameProfile;

public interface INicknameProvider {
	String getPlayerNickname(Player player);

	@NonNull
    String getProviderName();

	String getPlayerNickname(GameProfile player);
	@NonNull
    default String getPlayerChatName(GameProfile player) {
		
		String nick = getPlayerNickname(player);
		if(nick == null || nick.isEmpty()) return player.getName();
		else return nick;
	}
}