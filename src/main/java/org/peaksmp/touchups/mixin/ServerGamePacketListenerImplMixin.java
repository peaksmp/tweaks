package org.peaksmp.touchups.mixin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.TextFilter;
import org.peaksmp.touchups.Touchups;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Shadow public ServerPlayer player;

    @ModifyArg(
            method = "onDisconnect",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/ChatType;Ljava/util/UUID;)V")
    )
    private net.minecraft.network.chat.Component replaceLeaveMessage(net.minecraft.network.chat.Component text) {
        String name = this.player.getDisplayName().getString();
        Component message = MiniMessage.get().parse("<dark_gray>[<red>-<dark_gray>] <gray><name>", "name", name);
        return Touchups.get().adventure().toNative(message);
    }

    @ModifyVariable(
            method = "handleChat(Lnet/minecraft/server/network/TextFilter$FilteredText;)V",
            at = @At("STORE"),
            ordinal = 1
    )
    private net.minecraft.network.chat.Component reformatChat(net.minecraft.network.chat.Component value, TextFilter.FilteredText filteredText) {
        String name = this.player.getDisplayName().getString();
        String message = filteredText.getRaw();
        Component messageComponent = MiniMessage.get().parse("<gray><name>: <message>", "name", name, "message", message);
        return Touchups.get().adventure().toNative(messageComponent);
    }
}
