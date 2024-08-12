package carpet.mixins.tick.phase;

import carpet.tick.TickContext;
import carpet.tick.TickPhase;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.gen.WorldGeneratorType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
	@Unique
	private static final TickContext CONTEXT = TickContext.INSTANCE;

	@Inject(method = "loadWorld", at = @At("HEAD"))
	public void swapToInitialLoad(String saveName, String name, long seed, WorldGeneratorType generatorType, String generatorOptions, CallbackInfo ci) {
		CONTEXT.swapTickPhase(TickPhase.SERVER_INITIALIZE);
	}

	@Redirect(method = "tickWorlds", at = @At(value = "FIELD",
		target = "Lnet/minecraft/server/MinecraftServer;worlds:[Lnet/minecraft/server/world/ServerWorld;",
		opcode = 180 /* Opcodes.GETFIELD */, args = "array=get"))
	public ServerWorld swapToDimension(ServerWorld[] worlds, int index) {
		CONTEXT.swapTickingDimension(index);
		return worlds[index];
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void swapToTick(CallbackInfo ci) {
		CONTEXT.swapTickPhase(TickPhase.SERVER_TICK_COUNT);
	}

	@Inject(method = "tickWorlds", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/util/function/Supplier;)V"))
	public void swapToClientTimeSync(CallbackInfo ci) {
		CONTEXT.swapTickPhase(TickPhase.CLIENT_TIME_SYNC);
	}

	@Inject(method = "tickWorlds", at = @At(value = "INVOKE_STRING",
		target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
		args = "ldc=jobs"))
	public void swapToAsyncTasks(CallbackInfo ci) {
		CONTEXT.swapTickPhase(TickPhase.ASYNC_TASKS);
	}

	@Inject(method = "tickWorlds", at = @At(value = "INVOKE_STRING",
		target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
		args = "ldc=tracker"))
	public void swapToEntityTracking(CallbackInfo ci) {
		CONTEXT.swapTickPhase(TickPhase.ENTITY_TRACKING);
	}

	@Inject(method = "tickWorlds", at = @At(value = "INVOKE_STRING",
		target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
		args = "ldc=connection"))
	public void swapToConnection(CallbackInfo ci) {
		CONTEXT.swapTickingDimension(TickContext.DIMENSION_INDEPENDENT_ID);
		CONTEXT.swapTickPhase(TickPhase.CONNECTION_UPDATE);
	}

	@Inject(method = "tickWorlds", at = @At(value = "INVOKE_STRING",
		target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
		args = "ldc=players"))
	public void swapToPlayerList(CallbackInfo ci) {
		CONTEXT.swapTickPhase(TickPhase.PLAYER_LIST_TICK);
	}

	@Inject(method = "tickWorlds", at = @At(value = "INVOKE_STRING",
		target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
		args = "ldc=commandFunctions"))
	public void swapToCommandFunction(CallbackInfo ci) {
		CONTEXT.swapTickPhase(TickPhase.COMMAND_FUNCTION);
	}

	@Inject(method = "tick", at = @At(value = "INVOKE_STRING",
		target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
		args = "ldc=save"))
	public void swapToServerAutosave(CallbackInfo ci) {
		CONTEXT.swapTickPhase(TickPhase.SERVER_AUTO_SAVE);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	public void swapFromTick(CallbackInfo ci) {
		CONTEXT.swapTickPhase(null);
	}

	// Rainyaphtyl, can you explain to me why it is a good idea to add a tick phase
	// for server stop?
	@Inject(method = "stop", at = @At("HEAD"))
	public void swapToServerExit(CallbackInfo ci) {
		CONTEXT.swapTickPhase(TickPhase.SERVER_STOP);
	}
}
