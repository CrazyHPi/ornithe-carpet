package carpet.mixins.accessor;

import net.minecraft.util.BitStorage;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PalettedContainer.class)
public interface PalettedContainerA {
    @Accessor("storage")
    BitStorage getStorage();

    @Accessor("palette")
    Palette getPalette();

    @Accessor("bits")
    int getBits();
}
