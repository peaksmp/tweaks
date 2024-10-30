package org.peaksmp.tweaks;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.kyori.adventure.platform.modcommon.MinecraftServerAudiences;

public class Tweaks implements DedicatedServerModInitializer {
    private static Tweaks tweaks;
    private MinecraftServerAudiences adventure;

    public MinecraftServerAudiences adventure() throws IllegalStateException {
        if(this.adventure == null) throw new IllegalStateException("Tried to access Adventure without a running server!");
        return adventure;
    }

    public static Tweaks get() {
        return tweaks;
    }

    @Override
    public void onInitializeServer() {
        tweaks = this;

        ServerLifecycleEvents.SERVER_STARTING.register(server -> this.adventure = MinecraftServerAudiences.of(server));
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> this.adventure = null);
    }
}
