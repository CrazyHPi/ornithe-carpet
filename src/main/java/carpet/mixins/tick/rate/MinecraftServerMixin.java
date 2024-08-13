package carpet.mixins.tick.rate;

import carpet.tick.TickContext;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Unique
    private static TickContext CONTEXT = TickContext.INSTANCE;

    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;tick()V"))
    public void stepTick(CallbackInfo ci) {
        CONTEXT.tickTimer();
    }

    @ModifyConstant(method = "run", constant = @Constant(longValue = 2000L))
    public long modifyWarningThreshold(long constant) {
        return Math.max(2000L, CONTEXT.nanosPerTick / 25000L);
    }

    @ModifyArg(method = "run", at = @At(value = "INVOKE",
            target = "Lorg/apache/logging/log4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V",
            remap = false), index = 2)
    public Object modifyWarningMessage(String message, Object millis0, Object ticks0) {
        long millis = ((Number) millis0).longValue();
        return millis * 1000000L / CONTEXT.nanosPerTick;
    }

    @ModifyConstant(method = "run", constant = @Constant(longValue = 50L, ordinal = 1))
    public long computeMillisThisTick(long constant) {
        return CONTEXT.getMillisThisTick();
    }

    @ModifyConstant(method = "run", constant = @Constant(longValue = 50L, ordinal = 2))
    public long modifyMillisDecrement(long constant) {
        return CONTEXT.getMillisThisTick();
    }

    @ModifyConstant(method = "run", constant = @Constant(longValue = 50L, ordinal = 3))
    public long modifySleepBase(long constant) {
        return CONTEXT.getMillisThisTick();
    }
}
