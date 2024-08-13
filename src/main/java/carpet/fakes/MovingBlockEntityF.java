package carpet.fakes;

import net.minecraft.block.entity.BlockEntity;

public interface MovingBlockEntityF {
    BlockEntity getCarriedBlockEntity();

    void setCarriedBlockEntity(BlockEntity blockEntity);
}
