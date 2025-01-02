package com.rvt.bfcrmod.utils.moddeps;

import net.minecraft.world.entity.player.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import com.rvt.bfcrmod.utils.INicknameProvider;

import dev.ftb.mods.ftbessentials.util.FTBEPlayerData;


public class FTBNicknameProvider implements INicknameProvider {
	@Override public String getPlayerNickname(Player player) {
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
