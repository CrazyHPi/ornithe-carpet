package carpet.mixins.rule.lifetime;

import carpet.fakes.EntityF;
import carpet.helpers.lifetime.removal.LiteralRemovalReason;
import carpet.helpers.lifetime.removal.MobPickupRemovalReason;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Inject(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;remove()V", ordinal = 1)
    )
    private void recordDespawnTimeout(CallbackInfo ci) {
        ((EntityF) this).recordRemoval(LiteralRemovalReason.DESPAWN_TIMEOUT);
    }

    @Inject(
            method = "damage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;remove()V")
    )
    private void recordDeathSource(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ((EntityF) this).setDeathDamageSource(source);
    }

    @Inject(
            method = "canStack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;remove()V")
    )
    private void recordMerge(ItemEntity item, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = ((ItemEntity) (Object) this).getItemStack();
        int count = stack.getSize();
        stack.setSize(0);
        ((EntityF) this).recordRemoval(LiteralRemovalReason.MERGE);
        stack.setSize(count);
    }

    @Inject(
            method = "onPlayerCollision",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;remove()V")
    )
    private void recordPickup(PlayerEntity player, CallbackInfo ci) {
        ((EntityF) this).recordRemoval(new MobPickupRemovalReason(player.getClass()));
    }
}
