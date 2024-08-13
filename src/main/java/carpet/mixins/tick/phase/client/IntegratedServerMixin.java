package carpet.mixins.tick.phase.client;

import carpet.tick.TickContext;
import carpet.tick.TickPhase;
import net.minecraft.server.integrated.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin {
    @Unique
    private static final TickContext CONTEXT = TickContext.INSTANCE;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;updateViewDistance(I)V"))
    public void swapToViewDistanceAlteration(CallbackInfo ci) {
        CONTEXT.swapTickPhase(TickPhase.SP_VIEW_DISTANCE_ALT);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/world/ServerWorld;getData()Lnet/minecraft/world/WorldData;",
            ordinal = 0))
    public void swapToDifficultyAlteration(CallbackInfo ci) {
        CONTEXT.swapTickPhase(TickPhase.SP_DIFFICULTY_ALT);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE",
            target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V",
            args = "ldc=Saving and pausing game...", remap = false))
    public void swapToSaveOnPause(CallbackInfo ci) {
        CONTEXT.swapTickPhase(TickPhase.SP_SAVE_ON_PAUSE);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Ljava/util/Queue;isEmpty()Z"))
    public void swapToTaskOnPause(CallbackInfo ci) {
        CONTEXT.swapTickPhase(TickPhase.SP_TASK_ON_PAUSE);
    }
}
