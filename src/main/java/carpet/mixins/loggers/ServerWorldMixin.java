package carpet.mixins.loggers;

import carpet.logging.LoggerRegistry;
import carpet.logging.logHelpers.ExplosionLogHelper;
import carpet.utils.JavaVersionUtil;
import carpet.utils.Messenger;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ScheduledTick;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {
    @Shadow
    @Final
    private TreeSet<ScheduledTick> scheduledTicksInOrder;

    @Shadow
    @Final
    private MinecraftServer server;

    protected ServerWorldMixin(WorldStorage storage, WorldData data, Dimension dimension, Profiler profiler, boolean isClient) {
        super(storage, data, dimension, profiler, isClient);
    }

    @Inject(
            method = "doScheduledTicks",
            at = @At(
                    value = "CONSTANT",
                    args = "intValue=65536",
                    ordinal = 1
            )
    )
    private void logTileTickLimit(boolean flush, CallbackInfoReturnable<Boolean> cir) {
        if (LoggerRegistry.__tileTickLimit) {
            LoggerRegistry.getLogger("tileTickLimit").log(() -> new Text[]{
                    Messenger.s(String.format("Reached tile tick limit (%d > %d)", scheduledTicksInOrder.size(), 65536))
            });
        }
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

    private static final JavaVersionUtil.FieldAccessor<AtomicLong> SEED_ACCESSOR = JavaVersionUtil.objectFieldAccessor(Random.class, "seed", AtomicLong.class);
    @Unique
    public long getRandSeed(){
        return SEED_ACCESSOR.get(this.random).get();
    }

    // explosion logger
    // Solution for final explosion check -- not a great solution - CARPET-SYLKOS
    @Inject(method = "tick", at = @At("RETURN"))
    private void logLast(CallbackInfo ci) {
        ExplosionLogHelper.logLastExplosion();
    }
}
