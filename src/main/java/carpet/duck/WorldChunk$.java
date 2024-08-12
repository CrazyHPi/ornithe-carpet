package carpet.duck;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;

public interface WorldChunk$ {
	BlockState setBlockStateAndEntity(BlockPos pos, BlockState state, BlockEntity prescribedBE);
}
