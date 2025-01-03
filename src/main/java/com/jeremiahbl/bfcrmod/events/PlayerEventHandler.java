package com.jeremiahbl.bfcrmod.events;

import com.jeremiahbl.bfcrmod.config.ConfigHandler;
import com.jeremiahbl.bfcrmod.config.IReloadable;
import com.jeremiahbl.bfcrmod.config.PermissionsHandler;
import com.jeremiahbl.bfcrmod.config.PlayerData;
import com.jeremiahbl.bfcrmod.utils.BetterForgeChatUtilities;
import com.jeremiahbl.bfcrmod.BetterForgeChat;
import com.mojang.authlib.GameProfile;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent.LoadFromFile;
import net.minecraftforge.event.entity.player.PlayerEvent.NameFormat;
import net.minecraftforge.event.entity.player.PlayerEvent.SaveToFile;
import net.minecraftforge.event.entity.player.PlayerEvent.TabListNameFormat;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.io.IOException;

@EventBusSubscriber
public class PlayerEventHandler implements IReloadable {
	private boolean enableNicknamesInTabList = false;
	private boolean enableMetadataInTabList = false;
	
        @Override
	public void reloadConfigOptions() {
		enableNicknamesInTabList = ConfigHandler.config.enableNicknamesInTabList.get();
		enableMetadataInTabList = ConfigHandler.config.enableMetadataInTabList.get();
	}
	
	@SubscribeEvent
	public void onTabListNameFormatEvent(TabListNameFormat e) {
		BetterForgeChat.LOGGER.debug("Tablist formatting");
		if(ConfigHandler.config.enableTabListIntegration.get() && e.getEntity() != null && e.getEntity() instanceof ServerPlayer) {
			BetterForgeChat.LOGGER.debug("Tablist formatting enabled");
			GameProfile player = e.getEntity().getGameProfile();
			BetterForgeChat.LOGGER.debug("Tablist formatting for: "+ player);
			e.setDisplayName(BetterForgeChatUtilities.getFormattedPlayerName(player, 
				enableNicknamesInTabList && PermissionsHandler.playerHasPermission(player.getId(), PermissionsHandler.tabListNicknameNode),  
				enableMetadataInTabList  && PermissionsHandler.playerHasPermission(player.getId(), PermissionsHandler.tabListMetadataNode)));
		}
	}

	@SubscribeEvent
	public void onNameFormatEvent(NameFormat e) {
		if(e.getEntity() != null && e.getEntity() instanceof ServerPlayer)
			e.setDisplayname(BetterForgeChatUtilities.getFormattedPlayerName(e.getEntity().getGameProfile()));
	}

	@SubscribeEvent
	public void onSavePlayerData(SaveToFile e) {
		BetterForgeChat.LOGGER.debug("saving all Player Data");
		PlayerData.saveToDir(e.getPlayerDirectory());
	}

	@SubscribeEvent
	public void onLoadPlayerData(LoadFromFile e){
		BetterForgeChat.LOGGER.debug("Loading all Player Data");
		PlayerData.loadFromDir(e.getPlayerDirectory());
	}
}
