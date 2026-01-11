package carpet.mixins.tick.rate;

import carpet.fakes.MinecraftServerF;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.server.MinecraftServer.getTimeMillis;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements MinecraftServerF {
	@Shadow
	@Final
	private static Logger LOGGER;
	@Shadow
	public ServerWorld[] worlds;
	@Shadow
	private boolean running;
	@Shadow
	private long nextTickTime;
	@Shadow
	private long lastWarnTime;
	@Shadow
	private boolean loading;

	@Shadow
	public abstract void tick();

	/**
	 * To ensure compatibility with other mods we should allow milliseconds
	 */

	// Cancel a while statement
	@Redirect(
		method = "run",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/server/MinecraftServer;running:Z"
		)
	)
	private boolean cancelRunLoop(MinecraftServer server) {
		return false;
	} // target run()

	// Replaced the above cancelled while statement with this one
	// could possibly just inject that mspt selection at the beginning of the loop, but then adding all mspt's to
	// replace 50L will be a hassle
	@Inject(method = "run",
		at = @At(value = "INVOKE",
			shift = At.Shift.AFTER,
			target = "Lnet/minecraft/server/MinecraftServer;setStatus(Lnet/minecraft/server/ServerStatus;)V"
		)
	)
	private void modifiedRunLoop(CallbackInfo ci, @Local long l) throws InterruptedException {
		while (this.running) {
			if (this.getTickRateManager().isInWarpSpeed() && this.getTickRateManager().continueWarp()) {
				this.tick();
				this.nextTickTime = getTimeMillis();
				this.running = true;
				continue;
			}

			long m = getTimeMillis();
			long n = m - this.nextTickTime;
			long mspt = this.getTickRateManager().mspt();
			if (n > /*2000L*/1000L + 20 * mspt && this.nextTickTime - this.lastWarnTime >= /*15000L*/10000L + 100 * mspt) {
				LOGGER.warn("Can't keep up! Did the system time change, or is the server overloaded? Running {}ms behind, skipping {} tick(s)", n, n / mspt);
				n = 2000L;
				this.lastWarnTime = this.nextTickTime;
			}

			if (n < 0L) {
				LOGGER.warn("Time ran backwards! Did the system time change?");
				n = 0L;
			}

			l += n;
			this.nextTickTime = m;
			if (this.worlds[0].canSkipNight()) {
				this.tick();
				l = 0L;
			} else {
				while (l > mspt) {
					l -= mspt;
					this.tick();
				}
			}

			Thread.sleep(Math.max(1L, mspt - l));
			this.loading = true;
		}
	}
}
