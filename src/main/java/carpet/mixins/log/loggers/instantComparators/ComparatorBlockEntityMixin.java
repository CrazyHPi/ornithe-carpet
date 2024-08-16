package carpet.mixins.log.loggers.instantComparators;

import carpet.fakes.ComparatorBlockEntityF;
import net.minecraft.block.entity.ComparatorBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ComparatorBlockEntity.class)
public abstract class ComparatorBlockEntityMixin implements ComparatorBlockEntityF {
    @Unique
    private int[] scheduledOutputSignal = new int[3];

    @Unique
    private boolean[] buggy = new boolean[3];

    @Override
    public void setScheduledOutputSignal(int index, int value) {
        this.scheduledOutputSignal[index] = value;
    }

    @Override
    public int[] getScheduledOutputSignal() {
        return this.scheduledOutputSignal;
    }

    @Override
    public void setBuggy(int index, boolean value) {
        this.buggy[index] = value;
    }

    @Override
    public boolean[] getBuggy() {
        return this.buggy;
    }
}
