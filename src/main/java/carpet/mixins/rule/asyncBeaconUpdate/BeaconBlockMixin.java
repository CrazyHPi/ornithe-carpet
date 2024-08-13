package carpet.mixins.rule.asyncBeaconUpdate;

import carpet.CarpetSettings;
import net.minecraft.block.BeaconBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeaconBlock.class)
public abstract class BeaconBlockMixin {
    @Inject(method = "neighborChanged", at = @At("HEAD"))
    public void injectAsyncUpdate(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, CallbackInfo ci) {
        if (CarpetSettings.asyncBeaconUpdate && world.hasNeighborSignal(pos)) {
            HttpUtil.DOWNLOAD_THREAD_FACTORY.submit(() -> {
                world.updateNeighbors(pos, Blocks.BEACON, true);
            });
        }
    }
}
