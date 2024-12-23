package com.rvt.bfcrmod;

import com.rvt.bfcrmod.commands.NickCommands;
import com.mojang.logging.LogUtils;

import com.rvt.bfcrmod.config.ConfigHandler;
import com.rvt.bfcrmod.config.ConfigurationEventHandler;
import com.rvt.bfcrmod.config.PermissionsHandler;
import com.rvt.bfcrmod.events.ChatEventHandler;
import com.rvt.bfcrmod.events.CommandRegistrationHandler;
import com.rvt.bfcrmod.events.ExternalModLoadingEvent;
import com.rvt.bfcrmod.events.PlayerEventHandler;
import com.rvt.bfcrmod.utils.BetterForgeChatUtilities;
import com.rvt.bfcrmod.utils.IMetadataProvider;
import com.rvt.bfcrmod.utils.INicknameProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;

import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.event.lifecycle.ModLifecycleEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(BetterForgeChat.MODID )
public class BetterForgeChat {
	public static final String CHAT_ID_STR = 
			"&cBetter &9&lForge&r &eChat&r &d(c) Jeremiah Lowe, Disa Kandria 2022-2024&r\n";
	public static final String MODID = "bfcrmod";
	public static final String VERSION = "V1.0.3";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static BetterForgeChat instance;
	
    public IMetadataProvider metadataProvider = null;
    public INicknameProvider nicknameProvider = null;

    ChatEventHandler chatHandler = new ChatEventHandler();
    ExternalModLoadingEvent modLoadingEvent = new ExternalModLoadingEvent();
    PlayerEventHandler playerEventHandler = new PlayerEventHandler();

    public BetterForgeChat(IEventBus modBus, ModContainer container) {
    	instance = this;
    	// Register reloadable configuration stuff (reduce some things subscribed to the event bus)
        ConfigurationEventHandler configurationHandler = new ConfigurationEventHandler();
        PermissionsHandler permissionsHandler = new PermissionsHandler();
        CommandRegistrationHandler commandRegistrator = new CommandRegistrationHandler();

        configurationHandler.registerReloadable(playerEventHandler);
    	configurationHandler.registerReloadable(chatHandler);
    	configurationHandler.registerReloadable(() -> {
    		NickCommands.reloadConfig();
    		BetterForgeChatUtilities.reloadConfig();
    	});
    	configurationHandler.registerReloadable(() -> BetterForgeChat.LOGGER.info("Configuration options loaded!"));

        modBus.addListener(configurationHandler::onModConfigReloadingEvent);
        NeoForge.EVENT_BUS.addListener(configurationHandler::onServerStarted);

        container.registerConfig(ModConfig.Type.COMMON, ConfigHandler.spec);//set config type and config to register

        NeoForge.EVENT_BUS.register(permissionsHandler); //register permissions handle


        // Register server chat event
        //NeoForge.EVENT_BUS.register(chatHandler);
        // Register permissions mod API checking on server start
        //    NeoForge.EVENT_BUS.register(modLoadingEvent);
        // Register player events (NameFormat and TabListNameFormat)
        //    NeoForge.EVENT_BUS.register(playerEventHandler);
        // Final mod loading completion message

        // Register mod loading completed event
        modBus.addListener(this::loadComplete);
        NeoForge.EVENT_BUS.register(this); // Register the mod itself

        NeoForge.EVENT_BUS.register(commandRegistrator); // Register commands

    }

    @SubscribeEvent
    private void loadComplete(FMLLoadCompleteEvent e) {
    	// Register server chat event
    	NeoForge.EVENT_BUS.register(chatHandler);
    	// Register permissions mod API checking on server start
        NeoForge.EVENT_BUS.register(modLoadingEvent);
    	// Register player events (NameFormat and TabListNameFormat)
        NeoForge.EVENT_BUS.register(playerEventHandler);
        // Final mod loading completion message
    	LOGGER.info("Mod loaded up and ready to go! (c) Jeremiah Lowe, Disa Kandria 2022 - 2024!");
    }
}
