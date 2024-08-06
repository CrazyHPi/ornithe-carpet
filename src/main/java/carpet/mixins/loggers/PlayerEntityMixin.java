package carpet.mixins.loggers;

import carpet.logging.LoggerRegistry;
import carpet.logging.logHelpers.KillLogHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    private int mob_smashed;
    private boolean isSweeping;

    @Inject(method = "attack", at = @At("HEAD"))
    private void resetCount(Entity target, CallbackInfo ci) {
        mob_smashed = 1;
        isSweeping = false;
    }

    @Inject(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/living/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
            )
    )
    private void countSweep(Entity target, CallbackInfo ci) {
        mob_smashed++;
    }

    @Inject(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/living/player/PlayerEntity;doSweepAttackEffect()V"
            )
    )
    private void onSweep(Entity target, CallbackInfo ci) {
        if (LoggerRegistry.__kills) {
            isSweeping = true;
            KillLogHelper.onSweep(((PlayerEntity) (Object) this), mob_smashed);
        }
    }

    @Inject(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/living/player/PlayerEntity;doSweepAttackEffect()V",
                    shift = At.Shift.BY,
                    by = 2
            )
    )
    private void onNonSweep(Entity target, CallbackInfo ci) {
        if (LoggerRegistry.__kills && !isSweeping) {
            KillLogHelper.onNonSweepAttack(((PlayerEntity) (Object) this));
        }
    }
}
