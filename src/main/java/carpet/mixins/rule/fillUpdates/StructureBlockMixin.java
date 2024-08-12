package carpet.mixins.rule.fillUpdates;

import carpet.utils.MixinGlobals;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.entity.StructureBlockEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(StructureBlock.class)
public abstract class StructureBlockMixin {
    @WrapMethod(method = "activate")
    public void pushPopYeetUpdateFlags(StructureBlockEntity structureBlockEntity, Operation<Void> original) {
        MixinGlobals.pushYeetUpdateFlags();
        try {
            original.call(structureBlockEntity);
        } finally {
            MixinGlobals.restoreYeetUpdateFlags();
        }
    }
}
