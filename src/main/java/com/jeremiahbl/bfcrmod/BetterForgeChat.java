package com.jeremiahbl.bfcrmod;

import com.jeremiahbl.bfcrmod.commands.NickCommands;
import com.jeremiahbl.bfcrmod.config.*;
import com.jeremiahbl.bfcrmod.events.*;
import com.jeremiahbl.bfcrmod.utils.*;
import com.mojang.logging.LogUtils;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.slf4j.Logger;

import com.jeremiahbl.bfcrmod.utils.forgeutils.*;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(BetterForgeChat.MODID )
public class BetterForgeChat {
	public static final String CHAT_ID_STR = 
			"&cBetter &9&lForge&r &eChat&r &d(c) Jeremiah Lowe, Disa Kandria 2022-2024&r\n";
	public static final String MODID = "bfcrmod";
	public static final String VERSION = "V1.0.2";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static BetterForgeChat instance;
	
    public IMetadataProvider metadataProvider = null;
    public INicknameProvider nicknameProvider = null;
    
    private ChatEventHandler chatHandler = new ChatEventHandler();
    private ExternalModLoadingEvent modLoadingEvent = new ExternalModLoadingEvent();
    private PlayerEventHandler playerEventHandler = new PlayerEventHandler();
    private PermissionsHandler permissionsHandler = new PermissionsHandler();
    private CommandRegistrationHandler commandRegistrator = new CommandRegistrationHandler();
    private ConfigurationEventHandler configurationHandler = new ConfigurationEventHandler();
    
    public BetterForgeChat() {
    	instance = this;
    	// Register reloadable configuration stuff (reduce some things subscribed to the event bus)
    	configurationHandler.registerReloadable(playerEventHandler);
    	configurationHandler.registerReloadable(chatHandler);
    	configurationHandler.registerReloadable(() -> {
    		NickCommands.reloadConfig();
    		BetterForgeChatUtilities.reloadConfig();
    	});
    	configurationHandler.registerReloadable(() -> BetterForgeChat.LOGGER.info("Configuration options loaded!"));

    	forge_event.register(configurationHandler);

        // Set mod to ignore whether the other side has it (we only care about the server side - but this works on the client's side too)
        forge_fml.MlContext(true);
        //set config to register
        forge_fml.MLConfig("COMMON", ConfigHandler.spec);

    	forge_event.register(permissionsHandler); //register permissions handler

    	// Register mod loading completed event
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
        // Register the mod itself
        forge_event.register(this);
        // Register commands
        forge_event.register(commandRegistrator);

    }
    private void loadComplete(final FMLLoadCompleteEvent e) {
    	// Register server chat event
    	forge_event.register(chatHandler);
    	// Register permissions mod API checking on server start
        forge_event.register(modLoadingEvent);
    	// Register player events (NameFormat and TabListNameFormat)
        forge_event.register(playerEventHandler);
        // Final mod loading completion message
    	LOGGER.info("Mod loaded up and ready to go! (c) Jeremiah Lowe, Disa Kandria 2022 - 2024!");
    }
}
