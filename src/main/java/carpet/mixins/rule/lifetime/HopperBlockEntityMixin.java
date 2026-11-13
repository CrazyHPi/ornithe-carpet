package carpet.mixins.rule.lifetime;

import carpet.fakes.EntityF;
import carpet.helpers.lifetime.removal.LiteralRemovalReason;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin {
    @Inject(
            method = "pickUpItems",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;remove()V")
    )
    private static void recordHopperPickup(Inventory inventory, Inventory hopper, ItemEntity items, CallbackInfoReturnable<Boolean> cir) {
        ((EntityF) items).recordRemoval(LiteralRemovalReason.HOPPER);
    }
}
