package com.rvt.bfcrmod.config;

import java.util.ArrayList;

import com.rvt.bfcrmod.BetterForgeChat;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent.Reloading;

public class ConfigurationEventHandler {
	private final ArrayList<IReloadable> reloadables = new ArrayList<>();
	
	public void registerReloadable(IReloadable rel) {
		reloadables.add(rel);
	}
	
	public void reloadConfigOptions() {
		for(IReloadable reloadable : reloadables)
			if(reloadable != null)
				reloadable.reloadConfigOptions();
	}

	@SubscribeEvent//IModBusEvent
	public void onModConfigReloadingEvent(Reloading e) {
		reloadConfigOptions();
	}

	@SubscribeEvent //IEventBus
	public void onServerStarted(ServerStartedEvent e) {
		reloadConfigOptions();
	}

}
