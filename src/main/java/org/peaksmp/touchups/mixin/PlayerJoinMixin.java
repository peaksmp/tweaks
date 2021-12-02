package org.peaksmp.touchups.mixin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.peaksmp.touchups.Touchups;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class PlayerJoinMixin {
    @Unique private final ThreadLocal<ServerPlayer> cachedPlayer = new ThreadLocal<>();

    @Inject(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/ChatType;Ljava/util/UUID;)V"))
    private void setCachedPlayer(Connection connection, ServerPlayer player, CallbackInfo cli) {
        cachedPlayer.set(player);
    }

    @ModifyArg(
            method = "placeNewPlayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/ChatType;Ljava/util/UUID;)V", ordinal = 0)
    )
    private net.minecraft.network.chat.Component replaceServerMessage(net.minecraft.network.chat.Component text) {
        ServerPlayer player = cachedPlayer.get();
        cachedPlayer.remove();
        String name = player.getName().getString();
        Component message = MiniMessage.get().parse("<dark_gray>[<green>+<dark_gray>] <gray><name>", "name", name);
        return Touchups.get().adventure().toNative(message);
    }
}
