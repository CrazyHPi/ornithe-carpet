package carpet.mixins.tick.freeze.client;

import carpet.tick.TickContext;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.entity.living.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(World.class)
public abstract class WorldMixin {
    @Shadow @Final public boolean isClient;

	@Unique
	private static final TickContext CONTEXT = TickContext.INSTANCE;

	@WrapWithCondition(method = "tickEntities", at = @At(value = "FIELD",
		target = "Lnet/minecraft/entity/Entity;time:I", opcode = 181 /* PUTFIELD */))
	public boolean disableGlobalEntityTick0(Entity instance, int value) {
		return !isClient || !CONTEXT.frozen;
	}

	@WrapWithCondition(method = "tickEntities", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/entity/Entity;tick()V"))
	public boolean disableGlobalEntityTick1(Entity instance) {
		return !isClient || !CONTEXT.frozen;
	}

	@WrapWithCondition(method = "tickEntities", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/world/World;updateEntity(Lnet/minecraft/entity/Entity;)V"))
	public boolean disableRegularEntityTick(World instance, Entity entity) {
		return !isClient || !CONTEXT.frozen || entity instanceof ClientPlayerEntity;
	}
}
