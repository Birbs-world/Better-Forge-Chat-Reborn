package com.rvt.bfcrmod.events;

import com.rvt.bfcrmod.config.ConfigHandler;
import com.rvt.bfcrmod.config.IReloadable;
import com.rvt.bfcrmod.config.PermissionsHandler;
import com.rvt.bfcrmod.config.PlayerData;
import com.rvt.bfcrmod.utils.BetterForgeChatUtilities;
import com.rvt.bfcrmod.BetterForgeChat;

import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.LoadFromFile;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.NameFormat;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.SaveToFile;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.TabListNameFormat;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = BetterForgeChat.MODID)
public class PlayerEventHandler implements IReloadable {
	private static boolean enableNicknamesInTabList = false;
	private static boolean enableMetadataInTabList = false;
	private static boolean enableNameTagIntegration = false;
	private static boolean enableTabListIntegration = false;
	
	@Override
	public void reloadConfigOptions() {
		try {
			enableNicknamesInTabList = ConfigHandler.config.enableNicknamesInTabList.get();
			enableMetadataInTabList = ConfigHandler.config.enableMetadataInTabList.get();
			enableNameTagIntegration = ConfigHandler.config.enableNameTagIntegration.get();
			enableTabListIntegration = ConfigHandler.config.enableTabListIntegration.get();
		} catch (Exception e) {
			BetterForgeChat.LOGGER.error("Failed to load config options: ", e);
		}
	}
	public static boolean getEnableNameTagIntegration() {
		return enableNameTagIntegration;
	}
	
	@SubscribeEvent
	public static void onTabListNameFormatEvent(TabListNameFormat e) {
		if(enableTabListIntegration && (e.getEntity() instanceof ServerPlayer player)) {
			reloadPlayerName(player, false);
			e.setDisplayName(player.getCustomName());
		}
	}
	
	@SubscribeEvent
	public static void onNameFormatEvent(NameFormat e) {
		if(e.getEntity() instanceof ServerPlayer player) {
			reloadPlayerName(player, false);
			player.refreshTabListName();
		}
	}
	@SubscribeEvent
	public static void onSavePlayerData(SaveToFile e) {
		if (BetterForgeChat.instance.nicknameProvider != null && BetterForgeChat.instance.nicknameProvider.getProviderName().equals("BetterNeoForgeChat")) PlayerData.saveToDir(e.getPlayerDirectory());
	}
	@SubscribeEvent
	public static void onLoadPlayerData(LoadFromFile e) {
		if (BetterForgeChat.instance.nicknameProvider != null && BetterForgeChat.instance.nicknameProvider.getProviderName().equals("BetterNeoForgeChat")) PlayerData.loadFromDir(e.getPlayerDirectory());
	}
	
	public static void reloadPlayerName(@NotNull ServerPlayer player, boolean refresh) {
		BetterForgeChat.LOGGER.debug("Refreshing player name!");
		if (refresh) {
			player.refreshTabListName();
			player.refreshDisplayName();
		}
		player.setCustomName(BetterForgeChatUtilities.getFormattedPlayerName(player,
				enableNicknamesInTabList && PermissionsHandler.playerHasPermission(player.getUUID(), PermissionsHandler.tabListNicknameNode),
				enableMetadataInTabList && PermissionsHandler.playerHasPermission(player.getUUID(), PermissionsHandler.tabListMetadataNode)));
		player.setCustomNameVisible(true);
	}
}
