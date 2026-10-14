package com.rvt.bfcrmod;

import com.rvt.bfcrmod.commands.NickCommands;
import com.mojang.logging.LogUtils;

import com.rvt.bfcrmod.config.ConfigHandler;
import com.rvt.bfcrmod.config.ConfigurationEventHandler;
import com.rvt.bfcrmod.events.ChatEventHandler;
import com.rvt.bfcrmod.events.PlayerEventHandler;
import com.rvt.bfcrmod.utils.BetterForgeChatUtilities;
import com.rvt.bfcrmod.utils.IMetadataProvider;
import com.rvt.bfcrmod.utils.INicknameProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(BetterForgeChat.MODID)
public class BetterForgeChat {
	public static final String CHAT_ID_STR = 
			"&cBetter &9&lNeoForge&r &eChat&r \n&d(c) Disa Kandria 2024-2026, Jeremiah Lowe 2022-2024&r\n";
	public static final String MODID = "bfcrmod";
	public static final String VERSION = "V5.0.1";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static BetterForgeChat instance;
	
    public IMetadataProvider metadataProvider = null;
    public INicknameProvider nicknameProvider = null;
    
    public BetterForgeChat(IEventBus modBus, ModContainer container) {
        instance = this;
        /// v  what? this system seems extremely inefficient, it would be better to have all the config values reloaded from one class and just get all the config values from said class, i have seen mods that don't even need to initialize a separate field from the ones in the config itself, like Create -DDark47
        // Register reloadable configuration stuff (reduce some things subscribed to the event bus)
        ConfigurationEventHandler.registerReloadable(new PlayerEventHandler());
        ConfigurationEventHandler.registerReloadable(new ChatEventHandler());
        ConfigurationEventHandler.registerReloadable(new Parser());
        ConfigurationEventHandler.registerReloadable(new NickCommands());
        ConfigurationEventHandler.registerReloadable(new BetterForgeChatUtilities());
        
        container.registerConfig(ModConfig.Type.COMMON, ConfigHandler.spec);//set config type and config to register
        
        LOGGER.info("Mod loaded up and ready to go! (c) Disa Kandria 2024-2026!");
    }
}