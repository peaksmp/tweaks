package org.peaksmp.tweaks;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.world.chunk.ChunkManager;

public class Tweaks implements DedicatedServerModInitializer {
    private static Tweaks tweaks;
    private FabricServerAudiences adventure;

    public FabricServerAudiences adventure() {
        if(this.adventure == null) throw new IllegalStateException("Tried to access Adventure without a running server!");
        return adventure;
    }

    public static Tweaks get() {
        return tweaks;
    }

    @Override
    public void onInitializeServer() {
        tweaks = this;

        ServerLifecycleEvents.SERVER_STARTING.register(server -> this.adventure = FabricServerAudiences.of(server));
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> this.adventure = null);
    }
}
