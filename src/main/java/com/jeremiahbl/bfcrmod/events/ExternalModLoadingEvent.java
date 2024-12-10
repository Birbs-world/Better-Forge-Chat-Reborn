package com.jeremiahbl.bfcrmod.events;

import com.jeremiahbl.bfcrmod.BetterForgeChat;
import com.jeremiahbl.bfcrmod.commands.NickCommands;
import com.jeremiahbl.bfcrmod.config.ConfigHandler;
import com.jeremiahbl.bfcrmod.utils.IntegratedNicknameProvider;
import com.jeremiahbl.bfcrmod.utils.moddeps.FTBNicknameProvider;
import com.jeremiahbl.bfcrmod.utils.moddeps.LuckPermsProvider;

import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.loading.FMLEnvironment;

@EventBusSubscriber
public class ExternalModLoadingEvent {
	@SubscribeEvent public void onServerStarted(ServerStartedEvent e) {
		loadLuckPerms();
		loadFtbEssentials();
		loadIntegratedNicknameProvider();
		//loadDiscordIntegration();
	}
	/*private void loadDiscordIntegration() {
		if(ConfigHandler.config.enableDiscordBotIntegration.get()) {
			
		}
	}*/
	private void loadIntegratedNicknameProvider() {
		if (BetterForgeChat.instance.nicknameProvider == null && 
				ConfigHandler.config.autoEnableChatNicknameCommand.get()) {
			BetterForgeChat.instance.nicknameProvider = new IntegratedNicknameProvider();
			NickCommands.nicknameIntegrationEnabled = true;
    		BetterForgeChat.LOGGER.info("Integrated nickname management enabled successfully!");
		}
	}
	private void loadLuckPerms() {
		
		if(FMLEnvironment.dist.isDedicatedServer()) {
			BetterForgeChat.LOGGER.info("Detected loaded status of luckperms is :" + ModList.get().isLoaded("luckperms"));
			if(ModList.get().isLoaded("luckperms")) {

				if (!ConfigHandler.config.enableLuckPerms.get()) {
					BetterForgeChat.LOGGER.info("LuckPerms API was skipped by configuration file!");
					return;
				}
				BetterForgeChat.LOGGER.info("Attempting to integrate LuckPerms API!");
				try {
					BetterForgeChat.instance.metadataProvider = new LuckPermsProvider();
					BetterForgeChat.LOGGER.info("LuckPerms API found and integrated successfully!");

				} catch (Exception e) { // Could have a NoClassDefFoundError here!
					BetterForgeChat.instance.metadataProvider = null;
					BetterForgeChat.LOGGER.warn("OOPS something went wrong - LuckPerms wasn't found but the FML says its loaded, we won't use it!/nIf you see this warning please submit a issue report");
				}
			}else{
				BetterForgeChat.instance.metadataProvider = null;
				BetterForgeChat.LOGGER.warn("LuckPerms API wasn't found, we won't use it!");
			}
		}else{
			BetterForgeChat.instance.metadataProvider = null;
			BetterForgeChat.LOGGER.warn("Better Forge Chat Reborn is Running on client, Will not integrate Disabled LuckPerms API");
		}
	}
	private void loadFtbEssentials() {
		BetterForgeChat.LOGGER.info("Detected forge loaded status of FTB Essentials is :" + ModList.get().isLoaded("ftbessentials"));
		if(ModList.get().isLoaded("ftbessentials")) {
			if (!ConfigHandler.config.enableFtbEssentials.get()) {
				BetterForgeChat.LOGGER.info("FTB Essentials integration was skipped by configuration file!");
				return;
			}
			BetterForgeChat.LOGGER.info("Attempting to integrate FTB Essentials!");
			try {
				BetterForgeChat.instance.nicknameProvider = new FTBNicknameProvider();
				BetterForgeChat.LOGGER.info("FTB Essentials API found and integrated successfully!");

			} catch(Error e2) { // Could have a NoClassDefFoundError here!
				BetterForgeChat.instance.nicknameProvider = null;
				BetterForgeChat.LOGGER.warn("OOPS something went wrong - FTB Essentials wasn't found but the FML says its loaded, we won't use it!/nIf you see this warning please submit a issue report");
			}

    	}else {
			BetterForgeChat.instance.nicknameProvider = null;
			BetterForgeChat.LOGGER.warn("FTB Essentials wasn't found to be loaded, we won't use it!");
		}
	}
}
