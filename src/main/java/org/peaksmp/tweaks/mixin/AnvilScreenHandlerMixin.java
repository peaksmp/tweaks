package org.peaksmp.tweaks.mixin;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.peaksmp.tweaks.Tweaks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {

    @Shadow private String newItemName;

    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Shadow public abstract void updateResult();

    @Unique
    private static Text fromString(String string) {
        return Tweaks.get().adventure().toNative(MiniMessage.miniMessage().deserialize(string));
    }

    @Inject(method = {"setNewItemName"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setCustomName(Lnet/minecraft/text/Text;)Lnet/minecraft/item/ItemStack;", shift = At.Shift.AFTER)})
    public void setItemName(String name, CallbackInfo ci) {
        ItemStack itemStack = getSlot(2).getStack();
        itemStack.setCustomName(fromString(newItemName));
    }

    @Redirect(method = {"updateResult"}, at = @At(value = "INVOKE", target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z"))
    public boolean stringEqualOverride(String newItemName, Object ignored) {
        ItemStack itemStack = this.input.getStack(0);
        return fromString(newItemName).equals(itemStack.getName());
    }

    @Redirect(method = {"updateResult"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setCustomName(Lnet/minecraft/text/Text;)Lnet/minecraft/item/ItemStack;"))
    public ItemStack setName(ItemStack stack, Text name) {
        return stack.setCustomName(fromString(newItemName));
    }
}
