package carpet.mixins.rule.flattenedNoteBlocks;

import carpet.fakes.NoteBlockBlockEntityF;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.NoteBlockBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(NoteBlockBlockEntity.class)
public abstract class NoteBlockBlockEntityMixin extends BlockEntity implements NoteBlockBlockEntityF {
    @Unique
    private int instrument = -1;

    @Override
    public void setInstrument(int instrument) {
        int oldInstrument = this.getInstrument();
        this.instrument = instrument;
        if (instrument != oldInstrument) {
            world.updateObservers(pos, Blocks.NOTEBLOCK);
        }
    }

    @Override
    public int getInstrument() {
        if (instrument == -1) instrument = NoteBlockBlockEntityF.calculateInstrument(world.getBlockState(pos.down()));
        return instrument;
    }
}
