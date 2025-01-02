package com.rvt.bfcrmod.events;

import com.rvt.bfcrmod.config.ConfigHandler;
import com.rvt.bfcrmod.config.IReloadable;
import com.rvt.bfcrmod.config.PermissionsHandler;
import com.rvt.bfcrmod.config.PlayerData;
import com.rvt.bfcrmod.utils.BetterForgeChatUtilities;
import com.rvt.bfcrmod.BetterForgeChat;

import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.LoadFromFile;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.NameFormat;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.SaveToFile;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.TabListNameFormat;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class PlayerEventHandler implements IReloadable {
	private static boolean enableNicknamesInTabList = false;
	private static boolean enableMetadataInTabList = false;
	
        @Override
	public void reloadConfigOptions() {
		enableNicknamesInTabList = ConfigHandler.config.enableNicknamesInTabList.get();
		enableMetadataInTabList = ConfigHandler.config.enableMetadataInTabList.get();
	}
	
	@SubscribeEvent
	public static void onTabListNameFormatEvent(TabListNameFormat e) {
		if(ConfigHandler.config.enableTabListIntegration.get() && e.getEntity() != null && e.getEntity() instanceof ServerPlayer) {
			BetterForgeChat.LOGGER.debug("Tablist formatting enabled");
			Player player = e.getEntity();
			BetterForgeChat.LOGGER.debug("Tablist formatting for: "+ player);
			e.setDisplayName(BetterForgeChatUtilities.getFormattedPlayerName(player,
				enableNicknamesInTabList && PermissionsHandler.playerHasPermission(player.getUUID(), PermissionsHandler.tabListNicknameNode),
				enableMetadataInTabList  && PermissionsHandler.playerHasPermission(player.getUUID(), PermissionsHandler.tabListMetadataNode)));
		}
	}
	@SubscribeEvent
	public static void onNameFormatEvent(NameFormat e) {
		if(e.getEntity() != null && e.getEntity() instanceof ServerPlayer)
			e.setDisplayname(BetterForgeChatUtilities.getFormattedPlayerName(e.getEntity()));
	}
	@SubscribeEvent
	public static void onSavePlayerData(SaveToFile e) {
		PlayerData.saveToDir(e.getPlayerDirectory());
	}
	@SubscribeEvent
	public static void onLoadPlayerData(LoadFromFile e) {
		PlayerData.loadFromDir(e.getPlayerDirectory());
	}
}
