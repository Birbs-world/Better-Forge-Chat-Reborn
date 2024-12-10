package com.rvt.bfcrmod.events;

import com.rvt.bfcrmod.commands.BfcCommands;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class CommandRegistrationHandler {
	@SubscribeEvent
	public void registerCommands(RegisterCommandsEvent e) {
		BfcCommands.register(e.getDispatcher());
	}
}
