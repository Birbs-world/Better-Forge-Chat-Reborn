package com.rvt.bfcrmod.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rvt.bfcrmod.events.PlayerEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin {
    @ModifyVariable(
        method = "renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V",
        at = @At("HEAD"),
        argsOnly = true
    )
    protected Component renderNameTag(Component name, AbstractClientPlayer abstractClientPlayer, Component originalName, PoseStack poseStack, MultiBufferSource multiBufferSource) {
        return PlayerEventHandler.getEnableNameTagIntegration() ? abstractClientPlayer.getCustomName() : name;
    }
}
