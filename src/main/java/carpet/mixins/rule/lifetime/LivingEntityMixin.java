package carpet.mixins.rule.lifetime;

import carpet.fakes.EntityF;
import carpet.helpers.lifetime.spawning.MobDropSpawningReason;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "onKilled", at = @At("HEAD"))
    private void recordDeathSource(DamageSource source, CallbackInfo ci) {
        ((EntityF) this).setDeathDamageSource(source);
    }

    @WrapOperation(
            method = "tickPostDeath",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addEntity(Lnet/minecraft/entity/Entity;)Z")
    )
    private boolean recordXpOrbSpawning(World world, Entity entity, Operation<Boolean> original) {
        ((EntityF) entity).recordSpawning(new MobDropSpawningReason(((Entity) (Object) this).getClass()));
        return original.call(world, entity);
    }
}
