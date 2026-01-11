package carpet.mixins.accessor;

import net.minecraft.block.state.BlockState;
import net.minecraft.util.CrudeIncrementalIntIdentityHashMap;
import net.minecraft.world.chunk.HashMapPalette;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HashMapPalette.class)
public interface HashMapPaletteA {
    @Accessor("values")
    CrudeIncrementalIntIdentityHashMap<BlockState> getValues();
}
