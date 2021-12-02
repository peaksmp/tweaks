package org.peaksmp.touchups;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;

public class Touchups implements DedicatedServerModInitializer {
    private static Touchups touchups;
    private FabricServerAudiences adventure;

    public FabricServerAudiences adventure() {
        if(this.adventure == null) throw new IllegalStateException("Tried to access Adventure without a running server!");
        return adventure;
    }

    public static Touchups get() {
        return touchups;
    }

    @Override
    public void onInitializeServer() {
        touchups = this;

        ServerLifecycleEvents.SERVER_STARTING.register(server -> this.adventure = FabricServerAudiences.of(server));
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> this.adventure = null);
    }
}
