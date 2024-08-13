package carpet.mixins.tick.phase;

import carpet.tick.TickContext;
import carpet.tick.TickPhase;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BlockableEventLoop;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldData;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.storage.WorldStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements BlockableEventLoop {
    @Unique
    private static final TickContext CONTEXT = TickContext.INSTANCE;

    protected ServerWorldMixin(WorldStorage storage, WorldData data, Dimension dimension, Profiler profiler, boolean isClient) {
        super(storage, data, dimension, profiler, isClient);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldData;isHardcore()Z"))
    public void swapToHardcodeDifficulty(CallbackInfo ci) {
        CONTEXT.swapTickPhase(TickPhase.HARDCODE_DIFFICULTY);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;canSkipNight()Z"))
    public void swapToSleepDaytime(CallbackInfo ci) {
        CONTEXT.swapTickPhase(TickPhase.SLEEP_AND_DAYTIME);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldData;setTime(J)V"))
    public void swapToWorldTimeUpdate(CallbackInfo ci) {
        CONTEXT.swapTickPhase(TickPhase.WORLD_TIME_UPDATE);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE_STRING",
            target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
            args = "ldc=mobSpawner"))
    public void swapToSpawning(CallbackInfo ci) {
        CONTEXT.swapTickPhase(TickPhase.MOB_SPAWNING);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE_STRING",
            target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
            args = "ldc=chunkSource"))
    public void swapToChunkSource(CallbackInfo ci) {
        CONTEXT.swapTickPhase(TickPhase.CHUNK_UNLOAD);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE_STRING",
            target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
            args = "ldc=tickPending"))
    public void swapToTileTick(CallbackInfo ci) {
        CONTEXT.swapTickPhase(TickPhase.TILE_TICK);
    }

    @Inject(method = "tickChunks", at = @At(value = "HEAD"))
    public void swapToPlayerCheckLight(CallbackInfo ci) {
        CONTEXT.swapTickPhase(TickPhase.PLAYER_LIGHT_CHECK);
    }

    @Inject(method = "playerCheckLight", at = @At("TAIL"))
    public void swapToChunkTick(CallbackInfo ci) {
        CONTEXT.swapTickPhase(TickPhase.CHUNK_TICK);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE_STRING",
            target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
            args = "ldc=chunkMap"))
    public void swapToChunkMap(CallbackInfo ci) {
        CONTEXT.swapTickPhase(TickPhase.CHUNK_MAP);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE_STRING",
            target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
            args = "ldc=village"))
    public void swapToVillageTick(CallbackInfo ci) {
        CONTEXT.swapTickPhase(TickPhase.VILLAGE_TICK);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/village/VillageSiege;tick()V"))
    public void swapToVillageSiege(CallbackInfo ci) {
        CONTEXT.swapTickPhase(TickPhase.VILLAGE_SIEGE);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE_STRING",
            target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
            args = "ldc=portalForcer"))
    public void swapToPortalRemoval(CallbackInfo ci) {
        CONTEXT.swapTickPhase(TickPhase.PORTAL_REMOVAL);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/world/ServerWorld;doBlockEvents()V"))
    public void swapToBlockEvents(CallbackInfo ci) {
        CONTEXT.swapTickPhase(TickPhase.BLOCK_EVENT);
    }

    @Inject(method = "tickEntities", at = @At("HEAD"))
    public void swapToIdleTimeCheck(CallbackInfo ci) {
        CONTEXT.swapTickingDimension(this.dimension.getType().getId());
        CONTEXT.swapTickPhase(TickPhase.WORLD_IDLE_CHECK);
    }

    @Inject(method = "tickEntities", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/dimension/Dimension;tick()V"))
    public void swapToDragonFight(CallbackInfo ci) {
        CONTEXT.swapTickPhase(this.dimension.getType().getId() == 1 ? TickPhase.DRAGON_FIGHT : null);
    }
}
