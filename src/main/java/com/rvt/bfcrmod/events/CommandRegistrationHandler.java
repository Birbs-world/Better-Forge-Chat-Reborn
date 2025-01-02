package com.rvt.bfcrmod.events;

import com.rvt.bfcrmod.commands.BfcCommands;

import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class CommandRegistrationHandler {
	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent e) {
		BfcCommands.register(e.getDispatcher());
	}
}
