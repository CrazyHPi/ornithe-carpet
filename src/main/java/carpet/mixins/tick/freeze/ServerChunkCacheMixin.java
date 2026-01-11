package carpet.mixins.tick.freeze;

import carpet.fakes.MinecraftServerF;
import carpet.helpers.ServerTickRateManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.chunk.ServerChunkCache;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerChunkCache.class)
public class ServerChunkCacheMixin {
    @Shadow
    @Final
    private ServerWorld world;

    @Inject(
            method = "tick",
            at = @At(
                    "HEAD"
            ),
            cancellable = true
    )
    private void onTick(CallbackInfoReturnable<Boolean> cir) {
        ServerTickRateManager strm = ((MinecraftServerF) this.world.getServer()).getTickRateManager();
        if (!strm.runsNormally() && strm.deeplyFrozen()) {
            cir.setReturnValue(false);
        }
    }
}
