package carpet.mixins.rule.yeetUpdates;

import carpet.CarpetSettings;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public abstract class WorldMixin {
	@Inject(method = "updateNeighborComparators", at = @At("HEAD"), cancellable = true)
	public void yeetComparatorUpdates(BlockPos pos, Block block, CallbackInfo ci) {
		if (CarpetSettings.yeetComparatorUpdates) ci.cancel();
	}

	@Inject(method = "neighborChanged", at = @At("HEAD"), cancellable = true)
	public void yeetNeighborUpdates(BlockPos pos, Block neighborBlock, BlockPos neighborPos, CallbackInfo ci) {
		if (CarpetSettings.yeetNeighborUpdates) ci.cancel();
	}

	@Inject(method = "neighborStateChanged", at = @At("HEAD"), cancellable = true)
	public void yeetObserverUpdates(BlockPos pos, Block neighborBlock, BlockPos neighborPos, CallbackInfo ci) {
		if (CarpetSettings.yeetObserverUpdates) ci.cancel();
	}
}
