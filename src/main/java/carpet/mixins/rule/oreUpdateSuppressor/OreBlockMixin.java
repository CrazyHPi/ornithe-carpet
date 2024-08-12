package carpet.mixins.rule.oreUpdateSuppressor;

import carpet.SharedConstants;
import carpet.CarpetSettings;
import net.minecraft.block.Block;
import net.minecraft.block.OreBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(OreBlock.class)
public abstract class OreBlockMixin extends Block {
    protected OreBlockMixin() {
        super(SharedConstants.absurd());
    }

    @Intrinsic
    @SuppressWarnings("deprecation")
    @Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos) {
		if (CarpetSettings.oreUpdateSuppressor && world.hasNeighborSignal(pos))
			throw new ClassCastException("Carpet-simulated update suppression");
	}
}
