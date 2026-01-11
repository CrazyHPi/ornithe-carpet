package carpet.mixins.accessor;

import net.minecraft.util.BitStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BitStorage.class)
public interface BitStorageA {
    @Accessor("bits")
    int getBitsPerEntry();
}
