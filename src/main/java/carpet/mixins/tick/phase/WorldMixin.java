package carpet.mixins.tick.phase;

import carpet.tick.TickContext;
import carpet.tick.TickPhase;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public abstract class WorldMixin implements WorldView {
	@Shadow @Final
	public boolean isClient;

	@Unique
	private static final TickContext CONTEXT = TickContext.INSTANCE;

	@Inject(method = "tickWeather", at = @At("HEAD"))
	public void swapToWeatherUpdate(CallbackInfo ci) {
		if (!isClient) CONTEXT.swapTickPhase(TickPhase.WEATHER_UPDATE);
	}

	@Inject(method = "tickEntities", at = @At(value = "INVOKE_STRING",
		target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
		args = "ldc=global"))
	public void swapToGlobalEntities(CallbackInfo ci) {
		if (!isClient) CONTEXT.swapTickPhase(TickPhase.GLOBAL_ENTITY_UPDATE);
	}

	@Inject(method = "tickEntities", at = @At(value = "INVOKE_STRING",
		target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
		args = "ldc=remove"))
	public void swapToEntityChunkUpdate(CallbackInfo ci) {
		if (!isClient) CONTEXT.swapTickPhase(TickPhase.ENTITY_REMOVAL);
	}

	@Inject(method = "tickEntities", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/world/World;tickPlayers()V"))
	public void swapToPlayerEntityUpdate(CallbackInfo ci) {
		if (!isClient) CONTEXT.swapTickPhase(TickPhase.PLAYER_ENTITY_UPDATE);
	}

	@Inject(method = "tickEntities", at = @At(value = "INVOKE_STRING",
		target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
		args = "ldc=regular"))
	public void swapToEntityUpdate(CallbackInfo ci) {
		if (!isClient) CONTEXT.swapTickPhase(TickPhase.ENTITY_UPDATE);
	}

	@Inject(method = "tickEntities", at = @At(value = "INVOKE_STRING",
		target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
		args = "ldc=blockEntities"))
	public void swapToBlockEntityUnload(CallbackInfo ci) {
		if (!isClient) CONTEXT.swapTickPhase(TickPhase.BLOCK_ENTITY_UNLOAD);
	}

	@Inject(method = "tickEntities", at = @At(value = "FIELD",
		target = "Lnet/minecraft/world/World;isTickingBlockEntities:Z", ordinal = 0))
	public void swapToBlockEntityUpdate(CallbackInfo ci) {
		if (!isClient) CONTEXT.swapTickPhase(TickPhase.BLOCK_ENTITY_UPDATE);
	}

	@Inject(method = "tickEntities", at = @At(value = "INVOKE_STRING",
		target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
		args = "ldc=pendingBlockEntities"))
	public void swapToBlockEntityPending(CallbackInfo ci) {
		if (!isClient) CONTEXT.swapTickPhase(TickPhase.BLOCK_ENTITY_PENDING);
	}
}
