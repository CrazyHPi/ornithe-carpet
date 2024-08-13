package carpet.mixins.log.loggers.kills;

import carpet.api.log.LoggerRegistry;
import carpet.log.kills.KillLogHelper;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    private int mobSmashed;
    private boolean isSweeping;

    @Inject(method = "attack", at = @At("HEAD"))
    private void resetCount(Entity target, CallbackInfo ci) {
        mobSmashed = 1;
        isSweeping = false;
    }

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/living/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private void countSweep(Entity target, CallbackInfo ci) {
        mobSmashed++;
    }

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/living/player/PlayerEntity;doSweepAttackEffect()V"))
    private void onSweep(Entity target, CallbackInfo ci) {
        if (LoggerRegistry.__kills) {
            isSweeping = true;
            KillLogHelper.onSweep((PlayerEntity) (Object) this, mobSmashed);
        }
    }

    @Definition(id = "target", local = @Local(type = Entity.class, argsOnly = true))
    @Definition(id = "ServerPlayerEntity", type = ServerPlayerEntity.class)
    @Expression("target instanceof ServerPlayerEntity")
    @ModifyExpressionValue(method = "attack", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean onNonSweep(boolean original) {
        if (LoggerRegistry.__kills && !isSweeping) {
            KillLogHelper.onNonSweepAttack(((PlayerEntity) (Object) this));
        }
        return original;
    }
}
