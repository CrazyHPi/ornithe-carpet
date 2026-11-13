package carpet.mixins.rule.lifetime;

import carpet.fakes.EntityF;
import carpet.helpers.lifetime.removal.LiteralRemovalReason;
import net.minecraft.entity.living.mob.hostile.HostileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HostileEntity.class)
public abstract class HostileEntityMixin {
    @Inject(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/living/mob/hostile/HostileEntity;remove()V")
    )
    private void recordDifficultyDespawn(CallbackInfo ci) {
        ((EntityF) this).recordRemoval(LiteralRemovalReason.DESPAWN_DIFFICULTY);
    }
}
