package com.rvt.bfcrmod.utils;

import net.minecraft.world.entity.player.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import com.mojang.authlib.GameProfile;

public interface INicknameProvider {

	@NonNull
    String getProviderName();

	String getPlayerNickname(Player player);
	@NonNull
    default String getPlayerChatName(Player player) {
		
		String nick = getPlayerNickname(player);
		if(nick == null || nick.isEmpty()) return player.getName().getString();
		else return nick;
	}
}