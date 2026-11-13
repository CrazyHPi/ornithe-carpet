package carpet.mixins.rule.lifetime;

import carpet.fakes.EntityF;
import carpet.helpers.lifetime.spawning.LiteralSpawningReason;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.AnvilChunkStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilChunkStorage.class)
public abstract class AnvilChunkStorageMixin {
    @Inject(method = "addEntityWithPassengers", at = @At("HEAD"))
    private static void recordSpawnerSpawning(Entity entity, World world, CallbackInfo ci) {
        ((EntityF) entity).recordSpawning(LiteralSpawningReason.SPAWNER);
    }
}
