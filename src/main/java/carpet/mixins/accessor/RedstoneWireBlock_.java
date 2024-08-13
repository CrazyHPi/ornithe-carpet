package carpet.mixins.accessor;

import net.minecraft.block.RedstoneWireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RedstoneWireBlock.class)
public interface RedstoneWireBlock_ {
    @Accessor
    void setShouldSignal(boolean shouldSignal);
}
