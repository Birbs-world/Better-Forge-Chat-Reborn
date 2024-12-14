package com.jeremiahbl.bfcrmod.utils;

import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.common.MinecraftForge;

public class loader {

    static ModLoadingContext mlc = ModLoadingContext.get(); // Get the mod loading context (useful for doing stuff)
    public static void register(Object ToRegister){
        MinecraftForge.EVENT_BUS.register(ToRegister);
    }
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


}
