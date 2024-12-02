package com.jeremiahbl.bfcrmod.utils.moddeps;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.jeremiahbl.bfcrmod.utils.INicknameProvider;
import com.mojang.authlib.GameProfile;

import dev.ftb.mods.ftbessentials.util.FTBEPlayerData;

public class FTBNicknameProvider implements INicknameProvider {
	@Override public String getPlayerNickname(GameProfile player) {
		/* FTBEPlayerData data = FTBEPlayerData;
		if(data != null && data.getNick() != null && data.getNick().length() > 0)
			return data.getNick();
		return null; */
		FTBEPlayerData data = FTBEPlayerData.getOrCreate(player).orElse(null);
                if (data != null && !data.getNick().isEmpty()) {
                    return data.getNick();
		}
		return null;
	}
	@Override public @NonNull String getProviderName() {
		return "FTB Essentials";
	}
	
}
