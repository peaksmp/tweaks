package org.peaksmp.touchups.mixin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerStatusPacketListenerImpl;
import org.peaksmp.touchups.Touchups;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerStatusPacketListenerImpl.class)
public abstract class ServerStatusPacketListenerImplMixin {
    @Redirect(method = "handleStatusRequest", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getStatus()Lnet/minecraft/network/protocol/status/ServerStatus;"))
    public ServerStatus injectHandleStatusRequest(final MinecraftServer minecraftServer) {
        final ServerStatus vanillaStatus = minecraftServer.getStatus();

        final ServerStatus modifiedStatus = new ServerStatus();

        modifiedStatus.setDescription(vanillaStatus.getDescription());
        modifiedStatus.setFavicon(vanillaStatus.getFavicon());
        modifiedStatus.setVersion(vanillaStatus.getVersion());
        modifiedStatus.setPlayers(vanillaStatus.getPlayers());

        //TODO make this less weird to center text
        Component motd = MiniMessage.get().parse("                      <gradient:white:aqua:white>--- Peak ---</gradient>\n" +
                "         <gray>Welcome to the Caves and Cliffs update!</gray>");

        modifiedStatus.setDescription(Touchups.get().adventure().toNative(motd));

        return modifiedStatus;
    }
}
