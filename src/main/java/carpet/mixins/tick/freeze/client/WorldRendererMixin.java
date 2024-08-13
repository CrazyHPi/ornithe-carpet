package carpet.mixins.tick.freeze.client;

import carpet.tick.TickContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.entity.living.player.ClientPlayerEntity;
import net.minecraft.client.render.world.WorldRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
	@ModifyArg(method = "renderEntities", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;render(Lnet/minecraft/entity/Entity;FZ)V"))
	public float disableEntityInterpolation(Entity entity, float tickDelta, boolean hitbox) {
		return !TickContext.INSTANCE.frozen || entity instanceof ClientPlayerEntity
			? tickDelta : 0.0f;
	}

	@ModifyArg(method = "renderEntities", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/render/block/entity/BlockEntityRenderDispatcher;render(Lnet/minecraft/block/entity/BlockEntity;FI)V"))
	public float disableBlockEntityInterpolation(BlockEntity blockEntity, float tickDelta, int overlay) {
		return !TickContext.INSTANCE.frozen ? tickDelta : 0.0f;
	}
}
