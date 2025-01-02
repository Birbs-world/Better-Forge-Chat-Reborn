package com.rvt.bfcrmod.utils;

import com.rvt.bfcrmod.config.PlayerData;
import net.minecraft.world.entity.player.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import com.mojang.authlib.GameProfile;

public class IntegratedNicknameProvider implements INicknameProvider {
	@Override public String getPlayerNickname(@NonNull Player player) {
		return PlayerData.getNickname(player.getUUID());
	}

	@Override public @NonNull String getProviderName() {
		return "BetterForgeChat";
	}
}
