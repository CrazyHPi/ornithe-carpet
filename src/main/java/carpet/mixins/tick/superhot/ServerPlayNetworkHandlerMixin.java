package carpet.mixins.tick.superhot;

import carpet.fakes.MinecraftServerF;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.network.handler.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
	private static long lastMovedTick = 0L;
	private static double lastMoved = 0.0D;
	@Shadow
	public ServerPlayerEntity player;
	@Shadow
	private double firstGoodX;
	@Shadow
	private double firstGoodY;
	@Shadow
	private double firstGoodZ;

	@Inject(
		method = "handlePlayerInput",
		at = @At(
			"RETURN"
		)
	)
	private void checkMoves(PlayerInputC2SPacket packetIn, CallbackInfo ci) {
		if (packetIn.getSidewaysSpeed() != 0.0F || packetIn.getForwardSpeed() != 0.0F || packetIn.getJumping() || packetIn.getSneaking()) {
			((MinecraftServerF) this.player.getServer()).getTickRateManager().resetPlayerActivity();
		}
	}

	@Inject(
		method = "handlePlayerMove",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/entity/living/player/ServerPlayerEntity;isSleeping()Z",
			shift = At.Shift.BEFORE
		)
	)
	private void checkMove(PlayerMoveC2SPacket packet, CallbackInfo ci) {
		double movedBy = player.getSourcePos().squaredDistanceTo(firstGoodX, firstGoodY, firstGoodZ);
		if (movedBy == 0.0D) return;
		// corrective tick
		if (movedBy < 0.0009 && lastMoved > 0.0009 && Math.abs(player.getServer().getTicks() - lastMovedTick - 20) < 2) {
			return;
		}
		if (movedBy > 0.0D) {
			lastMoved = movedBy;
			lastMovedTick = player.getServer().getTicks();
			((MinecraftServerF) player.getServer()).getTickRateManager().resetPlayerActivity();
		}
	}
}
