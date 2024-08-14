package carpet.mixins.log.loggers.kills;

import carpet.log.framework.LoggerRegistry;
import carpet.log.loggers.kills.KillLogHelper;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Inject(method = "attack", at = @At("HEAD"))
    private void resetCount(Entity target, CallbackInfo ci,
                            @Share("sweep") LocalIntRef sweepFlagAndCount) {
        sweepFlagAndCount.set(1);
    }

    @Definition(id = "applyKnockback", method = "Lnet/minecraft/entity/living/LivingEntity;applyKnockback(Lnet/minecraft/entity/Entity;FDD)V")
    @Definition(id = "sin", method = "Lnet/minecraft/util/math/MathHelper;sin(F)F")
    @Definition(id = "cos", method = "Lnet/minecraft/util/math/MathHelper;cos(F)F")
    @Expression("?.applyKnockback(this, 0.4, (double) sin(?), (double) (-cos(?)))")
    @Inject(method = "attack", at = @At("MIXINEXTRAS:EXPRESSION"))
    private void countSweep(Entity target, CallbackInfo ci,
                            @Share("sweep") LocalIntRef sweepFlagAndCount) {
        sweepFlagAndCount.set(sweepFlagAndCount.get() + 1);
    }

    @Definition(id = "doSweepAttackEffect", method = "Lnet/minecraft/entity/living/player/PlayerEntity;doSweepAttackEffect()V")
    @Expression("this.doSweepAttackEffect()")
    @Inject(method = "attack", at = @At("MIXINEXTRAS:EXPRESSION"))
    private void onSweep(Entity target, CallbackInfo ci,
                         @Share("sweep") LocalIntRef sweepFlagAndCount) {
        if (LoggerRegistry.__kills) {
            int mobSmashed = sweepFlagAndCount.get();
            sweepFlagAndCount.set(-mobSmashed);
            KillLogHelper.onSweep((PlayerEntity) (Object) this, mobSmashed);
        }
    }

    @Definition(id = "target", local = @Local(type = Entity.class, argsOnly = true))
    @Definition(id = "ServerPlayerEntity", type = ServerPlayerEntity.class)
    @Expression("target instanceof ServerPlayerEntity")
    @ModifyExpressionValue(method = "attack", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean onNonSweep(boolean original, @Share("sweep") LocalIntRef sweepFlagAndCount) {
        if (LoggerRegistry.__kills && sweepFlagAndCount.get() > 0) {
            KillLogHelper.onNonSweepAttack(((PlayerEntity) (Object) this));
        }
        return original;
    }
}
