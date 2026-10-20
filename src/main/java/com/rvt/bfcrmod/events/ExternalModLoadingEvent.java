package com.rvt.bfcrmod.events;

import com.rvt.bfcrmod.BetterForgeChat;
import com.rvt.bfcrmod.commands.NickCommands;
import com.rvt.bfcrmod.config.ConfigHandler;
import com.rvt.bfcrmod.config.IReloadable;
import com.rvt.bfcrmod.utils.IntegratedNicknameProvider;
import com.rvt.bfcrmod.utils.moddeps.FTBNicknameProvider;
import com.rvt.bfcrmod.utils.moddeps.LuckPermsProvider;

import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;

@EventBusSubscriber(modid =  BetterForgeChat.MODID)
public class ExternalModLoadingEvent implements IReloadable {
	private static boolean autoEnableChatNicknameCommand = false;
	private static boolean enableLuckPerms = false;
	private static boolean enableFtbEssentials = false;
	
	@Override
	public void reloadConfigOptions() {
		try {
			autoEnableChatNicknameCommand = ConfigHandler.config.autoEnableChatNicknameCommand.get();
			enableLuckPerms = ConfigHandler.config.enableLuckPerms.get();
			enableFtbEssentials = ConfigHandler.config.enableFtbEssentials.get();
		} catch (Exception e) {
			BetterForgeChat.LOGGER.error("Failed to load config options: ", e);
		}
	}
	
	@SubscribeEvent public static void onServerStarted(ServerStartedEvent e) {
		loadLuckPerms();
		loadFtbEssentials();
		loadIntegratedNicknameProvider();
	}
	private static void loadIntegratedNicknameProvider() {
		if (BetterForgeChat.instance.nicknameProvider == null && autoEnableChatNicknameCommand) {
			BetterForgeChat.instance.nicknameProvider = new IntegratedNicknameProvider();
			NickCommands.nicknameIntegrationEnabled = true;
    		BetterForgeChat.LOGGER.info("Integrated nickname management enabled successfully!");
		}
	}
	private static void loadLuckPerms() {
		if(FMLEnvironment.dist.isDedicatedServer()) {
            BetterForgeChat.LOGGER.debug("Detected loaded status of luckperms is :{}", ModList.get().isLoaded("luckperms"));
			if(ModList.get().isLoaded("luckperms") && enableLuckPerms) {
				BetterForgeChat.LOGGER.info("Attempting to integrate LuckPerms API!");
				try {
					BetterForgeChat.instance.metadataProvider = new LuckPermsProvider();
					BetterForgeChat.LOGGER.info("LuckPerms API found and integrated successfully!");

				} catch (Exception e) { // Could have a NoClassDefFoundError here!
					BetterForgeChat.instance.metadataProvider = null;
					BetterForgeChat.LOGGER.warn("OOPS something went wrong - LuckPerms wasn't found but the FML says its loaded, we won't use it!/nIf you see this warning please submit a issue report");
				}
			} else {
				BetterForgeChat.instance.metadataProvider = null;
				BetterForgeChat.LOGGER.warn("LuckPerms API wasn't found, or the config value was disabled, we won't use it!");
			}
		} else {
			BetterForgeChat.instance.metadataProvider = null;
			BetterForgeChat.LOGGER.warn("Better NeoForge Chat Reborn is Running on client, Will not integrate Disabled LuckPerms API");
		}
	}
	private static void loadFtbEssentials() {
        BetterForgeChat.LOGGER.debug("Detected loaded status of FTB Essentials is :{}", ModList.get().isLoaded("ftbessentials"));
		if(ModList.get().isLoaded("ftbessentials") && enableFtbEssentials) {
			BetterForgeChat.LOGGER.info("Attempting to integrate FTB Essentials!");
			try {
				BetterForgeChat.instance.nicknameProvider = new FTBNicknameProvider();
				BetterForgeChat.LOGGER.info("FTB Essentials API found and integrated successfully!");
			} catch(Error e2) { // Could have a NoClassDefFoundError here!
				BetterForgeChat.instance.nicknameProvider = null;
				BetterForgeChat.LOGGER.warn("OOPS something went wrong - FTB Essentials wasn't found but the FML says its loaded, we won't use it!/nIf you see this warning please submit a issue report");
			}
    	} else {
			BetterForgeChat.instance.nicknameProvider = null;
			BetterForgeChat.LOGGER.warn("FTB Essentials wasn't found to be loaded, or the config toggle was disabled, we won't use it!");
		}
	}
}
