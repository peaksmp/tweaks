package org.peaksmp.tweaks.mixin;

import net.kyori.adventure.platform.fabric.FabricServerAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.peaksmp.tweaks.Tweaks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {

    @Shadow private String itemName;

    public AnvilMenuMixin(@Nullable MenuType<?> menuType, int i, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
        super(menuType, i, inventory, containerLevelAccess);
    }

    @Shadow public abstract void createResult();

    @Unique
    private static Component componentFromString(String string) throws IllegalStateException {
        try(FabricServerAudiences audiences = Tweaks.get().adventure()) {
            return audiences.toNative(MiniMessage.miniMessage().deserialize(string));
        }
    }

    @Redirect(
            method = {"createResult"},
            at = @At(value = "INVOKE", target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z")
    )
    public boolean stringEqualOverride(String newItemName, Object ignored) {
        ItemStack itemStack = this.inputSlots.getItem(0);
        return componentFromString(newItemName).equals(itemStack.getHoverName());
    }

    @SuppressWarnings("unchecked")
    @Redirect(
            method = {"createResult"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;set(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;")
    )
    public <T> T setName(ItemStack itemStack, DataComponentType<? super T> component, T value) {
        return (T) itemStack.set(DataComponents.CUSTOM_NAME, componentFromString(itemName));
    }
}
