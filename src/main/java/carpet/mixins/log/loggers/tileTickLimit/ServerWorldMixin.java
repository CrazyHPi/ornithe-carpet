package carpet.mixins.log.loggers.tileTickLimit;

import carpet.log.framework.LoggerRegistry;
import carpet.utils.Messenger;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.TreeSet;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {
    @Shadow
    @Final
    private TreeSet<ScheduledTick> scheduledTicksInOrder;

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
}
