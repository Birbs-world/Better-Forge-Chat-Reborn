package com.rvt.bfcrmod.config;

import java.util.ArrayList;

import com.rvt.bfcrmod.BetterForgeChat;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.bus.api.SubscribeEvent;

@EventBusSubscriber(modid = BetterForgeChat.MODID)
public class ConfigurationEventHandler {
	private static final ArrayList<IReloadable> reloadables = new ArrayList<>();
	
	public static void registerReloadable(IReloadable rel) {
		reloadables.add(rel);
	}
	
	public static void reloadConfigOptions() {
		for(IReloadable reloadable : reloadables)
			if(reloadable != null && ConfigHandler.spec.isLoaded())
				reloadable.reloadConfigOptions();
	}

	@SubscribeEvent
	public static void onModConfigReloadingEvent(ModConfigEvent.Reloading e) {
		reloadConfigOptions();
	}

	@SubscribeEvent
	public static void onServerStarted(ModConfigEvent.Loading e) {
		reloadConfigOptions();
	}

}
