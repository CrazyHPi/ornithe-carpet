package carpet.mixins.rule.lifetime;

import carpet.fakes.EntityF;
import carpet.helpers.lifetime.removal.LiteralRemovalReason;
import carpet.helpers.lifetime.removal.MobPickupRemovalReason;
import net.minecraft.entity.XpOrbEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(XpOrbEntity.class)
public abstract class XpOrbEntityMixin {
    @Inject(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/XpOrbEntity;remove()V")
    )
    private void recordDespawnTimeout(CallbackInfo ci) {
        ((EntityF) this).recordRemoval(LiteralRemovalReason.DESPAWN_TIMEOUT);
    }

    @Inject(
            method = "damage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/XpOrbEntity;remove()V")
    )
    private void recordDeathSource(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ((EntityF) this).setDeathDamageSource(source);
    }

    @Inject(
            method = "onPlayerCollision",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/XpOrbEntity;remove()V")
    )
    private void recordPickup(PlayerEntity player, CallbackInfo ci) {
        ((EntityF) this).recordRemoval(new MobPickupRemovalReason(player.getClass()));
    }
}
