package carpet.mixins.rule.missingTools;

import carpet.CarpetSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;

@Mixin(PickaxeItem.class)
public abstract class PickaxeItemMixin extends ToolItem {
    protected PickaxeItemMixin(Tier material, Set<Block> effectiveBlocks) {
        super(material, effectiveBlocks);
    }

    @WrapOperation(
        method = "getMiningSpeed",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ToolItem;getMiningSpeed(Lnet/minecraft/item/ItemStack;Lnet/minecraft/block/state/BlockState;)F"
        )
    )
    private float getMissingToolMiningSpeed(PickaxeItem instance, ItemStack stack, BlockState state,
                                            Operation<Float> original) {
        if (CarpetSettings.missingTools) {
            Material material = state.getMaterial();
            if (material == Material.PISTON || material == Material.GLASS) {
                return this.miningSpeed;
            }
        }
        return original.call(instance, stack, state);
    }

}
