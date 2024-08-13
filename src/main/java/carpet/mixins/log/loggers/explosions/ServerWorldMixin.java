package carpet.mixins.log.loggers.explosions;

import carpet.log.loggers.explosions.ExplosionLogHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldData;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.storage.WorldStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {
    protected ServerWorldMixin(WorldStorage storage, WorldData data, Dimension dimension, Profiler profiler, boolean isClient) {
        super(storage, data, dimension, profiler, isClient);
    }

    // explosion logger
    // Solution for final explosion check -- not a great solution - CARPET-SYLKOS
    @Inject(method = "tick", at = @At("RETURN"))
    private void logLast(CallbackInfo ci) {
        ExplosionLogHelper.logLastExplosion();
    }
}
