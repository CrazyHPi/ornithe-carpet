package carpet.duck;

import net.minecraft.block.entity.BlockEntity;

public interface MovingBlockEntity$ {
    BlockEntity getCarriedBlockEntity();

    void setCarriedBlockEntity(BlockEntity blockEntity);
}
