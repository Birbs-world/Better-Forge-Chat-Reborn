package com.jeremiahbl.bfcrmod.utils.forgeutils;

import net.minecraftforge.common.MinecraftForge;

/*import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.LoadFromFile;
import net.minecraftforge.event.entity.player.PlayerEvent.NameFormat;
import net.minecraftforge.event.entity.player.PlayerEvent.SaveToFile;
import net.minecraftforge.event.entity.player.PlayerEvent.TabListNameFormat;

import net.minecraftforge.eventbus.api.SubscribeEvent;
*/
public class forge_event {
    public static void register(Object ToRegister){
        MinecraftForge.EVENT_BUS.register(ToRegister);
    }
    
}
