package carpet.mixins.log.loggers.rng;

import carpet.SharedConstants;
import carpet.log.framework.LoggerRegistry;
import carpet.utils.Messenger;
import carpet.utils.ReflectionUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldData;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.storage.WorldStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {
    @Shadow
    @Final
    private MinecraftServer server;

    protected ServerWorldMixin(WorldStorage storage, WorldData data, Dimension dimension, Profiler profiler, boolean isClient) {
        super(storage, data, dimension, profiler, isClient);
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ServerWorld;doScheduledTicks(Z)Z",
                    shift = At.Shift.AFTER
            )
    )
    private void logRngTT(CallbackInfo ci) {
        if (LoggerRegistry.__rng) {
            LoggerRegistry.getLogger("rng").log(() -> new Text[]{
                    Messenger.s(String.format("RNG TickUp. t:%d seed:%d d:%s", server.getTicks(), getRandSeed(), dimension.getType().name()))
            });
        }
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ServerWorld;doBlockEvents()V",
                    shift = At.Shift.AFTER
            )
    )
    private void logRngBE(CallbackInfo ci) {
        if (LoggerRegistry.__rng) {
            LoggerRegistry.getLogger("rng").log(() -> new Text[]{
                    Messenger.s(String.format("RNG BlockEv. t:%d seed:%d d:%s", server.getTicks(), getRandSeed(), dimension.getType().name()))
            });
        }
    }

    @Unique
    private long getRandSeed() {
        Optional<AtomicLong> seed = ReflectionUtil.getObjectField(this.random, "seed");
        if (!seed.isPresent()) {
            SharedConstants.LOG.warn("Can not get seed, is server running Java >8?");
            return -1;
        }
        return seed.get().get();
    }
}
