package carpet.mixins.tick.freeze;

import carpet.tick.TickContext;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.server.world.PortalForcer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BlockableEventLoop;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.NaturalSpawner;
import net.minecraft.world.World;
import net.minecraft.world.WorldData;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.storage.WorldStorage;
import net.minecraft.world.village.SavedVillageData;
import net.minecraft.world.village.VillageSiege;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ServerWorld.class, priority = 100)
public abstract class ServerWorldMixin extends World implements BlockableEventLoop {
	protected ServerWorldMixin(WorldStorage storage, WorldData data, Dimension dimension, Profiler profiler, boolean isClient) {
		super(storage, data, dimension, profiler, isClient);
	}

	@Unique
	private static final TickContext CONTEXT = TickContext.INSTANCE;

	// I love you MixinExtras, I love you LlamaLad7
	// If I don't have that handy WrapOperation thingy I would have to use Overwrite

	@WrapWithCondition(method = "tick", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/world/World;tick()V"))
	public boolean wrapTickWeather(World instance) {
		return !CONTEXT.frozen;
	}

	@WrapWithCondition(method = "tick", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/world/NaturalSpawner;tick(Lnet/minecraft/server/world/ServerWorld;ZZZ)I"))
	public boolean wrapMobSpawning(NaturalSpawner instance, ServerWorld world, boolean spawnAnimals, boolean spawnMonsters, boolean spawnRareMobs) {
		return !CONTEXT.frozen;
	}

	@WrapWithCondition(method = "tick", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/world/WorldData;setTime(J)V"))
	public boolean wrapWorldTimeUpdate(WorldData instance, long time) {
		return !CONTEXT.frozen;
	}
	@WrapWithCondition(method = "tick", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/world/WorldData;setTimeOfDay(J)V"))
	public boolean wrapDayTimeUpdate(WorldData instance, long time) {
		return !CONTEXT.frozen;
	}

	// Chunk unload intentionally left unfrozen, to make chunks properly unload obv

	@WrapWithCondition(method = "tick", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/server/world/ServerWorld;doScheduledTicks(Z)Z"))
	public boolean wrapTileTicks(ServerWorld instance, boolean flush) {
		return !CONTEXT.frozen;
	}

	@WrapWithCondition(method = "tick", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/server/world/ServerWorld;tickChunks()V"))
	public boolean wrapChunkTicks(ServerWorld instance) {
		return !CONTEXT.frozen;
	}

	// Chunk map intentionally left unfrozen, to let player load chunks
	// I guess we'll freeze chunk maps when we implement creativePlayerLoadChunks?

	@WrapWithCondition(method = "tick", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/world/village/SavedVillageData;tick()V"))
	public boolean wrapVillages(SavedVillageData instance) {
		return !CONTEXT.frozen;
	}

	@WrapWithCondition(method = "tick", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/world/village/VillageSiege;tick()V"))
	public boolean wrapVillageSieges(VillageSiege instance) {
		return !CONTEXT.frozen;
	}

	@WrapWithCondition(method = "tick", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/server/world/PortalForcer;tick(J)V"))
	public boolean wrapPortalRemoval(PortalForcer instance, long time) {
		return !CONTEXT.frozen;
	}

	@WrapWithCondition(method = "tick", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/server/world/ServerWorld;doBlockEvents()V"))
	public boolean wrapBlockEvents(ServerWorld instance) {
		return !CONTEXT.frozen;
	}
}
