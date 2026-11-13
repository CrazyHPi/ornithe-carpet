package carpet.mixins.rule.lifetime;

import carpet.fakes.EntityF;
import carpet.fakes.ServerWorldF;
import carpet.helpers.lifetime.spawning.LiteralSpawningReason;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.NaturalSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NaturalSpawner.class)
public abstract class NaturalSpawnerMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void increaseSpawnStage(ServerWorld world, boolean spawnAnimals, boolean spawnMonsters, boolean spawnRareMobs, CallbackInfoReturnable<Integer> cir) {
        ((ServerWorldF) world).getLifeTimeWorldTracker().increaseSpawnStageCounter();
    }

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;addEntity(Lnet/minecraft/entity/Entity;)Z")
    )
    private boolean recordNaturalSpawning(ServerWorld world, Entity entity, Operation<Boolean> original) {
        ((EntityF) entity).recordSpawning(LiteralSpawningReason.NATURAL);
        return original.call(world, entity);
    }
}
