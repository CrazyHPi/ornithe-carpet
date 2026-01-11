package carpet.mixins.accessor;

import net.minecraft.world.chunk.LinearPalette;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LinearPalette.class)
public interface LinearPaletteA {
    @Accessor("size")
    int paletteSize();
}
