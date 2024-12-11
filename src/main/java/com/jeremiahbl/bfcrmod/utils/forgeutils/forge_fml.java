package com.jeremiahbl.bfcrmod.utils.forgeutils;

import com.jeremiahbl.bfcrmod.commands.NickCommands;
import com.jeremiahbl.bfcrmod.config.*;
import com.jeremiahbl.bfcrmod.events.*;
import com.jeremiahbl.bfcrmod.utils.*;
import com.mojang.logging.LogUtils;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;

import org.slf4j.Logger;

import com.jeremiahbl.bfcrmod.utils.forgeutils.*;

public class forge_fml {

    static ModLoadingContext mlc = ModLoadingContext.get(); // Get the mod loading context (useful for doing stuff)


    public static void MlContext(boolean context){
        // Set mod to ignore whether the other side has it (we only care about the server side - but this works on the client's side too)
        mlc.registerExtensionPoint(
                IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(
                        () -> NetworkConstants.IGNORESERVERONLY, (a, b) -> context
                )
        );
    }

    // Register mod configuration & permissions
    public static void MLConfig(String cType, IConfigSpec<?> config){
        mlc.registerConfig(ModConfig.Type.valueOf(cType), config);
    }
    public static void JavaContext(){

    }


}