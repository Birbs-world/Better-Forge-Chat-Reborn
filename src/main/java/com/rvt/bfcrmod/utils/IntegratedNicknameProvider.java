package com.rvt.bfcrmod.utils;

import com.rvt.bfcrmod.config.PlayerData;
import net.minecraft.world.entity.player.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import com.mojang.authlib.GameProfile;

public class IntegratedNicknameProvider implements INicknameProvider {
	@Override public String getPlayerNickname(@NonNull GameProfile player) {
		return PlayerData.getNickname(player.getId());
	}

	@Override
	public String getPlayerNickname(Player player) {
		return "";
	}

	@Override public @NonNull String getProviderName() {
		return "BetterForgeChat";
	}
}
