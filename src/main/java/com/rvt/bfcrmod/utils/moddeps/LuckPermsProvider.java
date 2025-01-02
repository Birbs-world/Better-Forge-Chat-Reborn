package com.rvt.bfcrmod.utils.moddeps;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.rvt.bfcrmod.utils.IMetadataProvider;
import com.mojang.authlib.GameProfile;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;

public class LuckPermsProvider implements IMetadataProvider {
	private final LuckPerms luckPerms;
	private static LuckPermsProvider instance;

	public LuckPermsProvider() {
		instance = this;
		this.luckPerms = net.luckperms.api.LuckPermsProvider.get();
	}
	public static LuckPermsProvider getInstance() {
		return instance;
	}

	private CachedMetaData getMetaData(GameProfile player) {
		if (this.luckPerms == null) {
			return null;
		} else {
			User user = this.luckPerms.getUserManager().getUser(player.getId());
			return user != null ? user.getCachedData().getMetaData() : null;
		}
	}

        @Override
	public String[] getPlayerPrefixAndSuffix(GameProfile player) {
		try {
			CachedMetaData metaData = this.getMetaData(player);
            assert metaData != null;
            return new String[]{metaData.getPrefix(), metaData.getSuffix()};
		} catch(IllegalStateException ise) {
			return null;
		}
	}

	public @NonNull@Override
 String getProviderName() {
		return "LuckPerms";
	}
}
