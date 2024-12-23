package com.rvt.bfcrmod.utils;

import net.minecraft.world.entity.player.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import com.mojang.authlib.GameProfile;

public interface IMetadataProvider {
	@NonNull public String getProviderName();
	@NonNull public default String getPlayerPrefix(@NonNull Player player) { return getPlayerPrefixAndSuffix(player)[0]; }
	@NonNull public default String getPlayerSuffix(@NonNull Player player) { return getPlayerPrefixAndSuffix(player)[1]; }
	@NonNull public String[] getPlayerPrefixAndSuffix(@NonNull Player player);
}
