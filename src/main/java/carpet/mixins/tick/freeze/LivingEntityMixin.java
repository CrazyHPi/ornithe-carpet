package carpet.mixins.tick.freeze;

import carpet.fakes.MinecraftServerF;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(
            method = "pushAwayCollidingEntities",
            at = @At(
                    "HEAD"
            ),
            cancellable = true
    )
    private void onPushEntities(CallbackInfo ci) {
        if ((LivingEntity) (Object) this instanceof ServerPlayerEntity) {
            MinecraftServer server = ((LivingEntity) (Object) this).world.getServer();
            if (((MinecraftServerF) server).getTickRateManager().gameIsPaused()) {
                ci.cancel();
            }
        }
    }
}

