package carpet.mixins.rule.noItemCost;

import carpet.CarpetSettings;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DispenserBlock.class)
public abstract class DispenserBlockMixin {
    @Redirect(method = "dispense", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/DispenserBlockEntity;setStack(ILnet/minecraft/item/ItemStack;)V"))
    public void setStackNoItemCost(DispenserBlockEntity blockEntity, int slot, ItemStack stack) {
        if ("none".equals(CarpetSettings.dispenserNoItemCost)) {
            blockEntity.setStack(slot, stack);
        } else if ("wool".equals(CarpetSettings.dispenserNoItemCost) &&
                blockEntity.getWorld().getBlockState(blockEntity.getPos().down()).getBlock() == Blocks.WOOL) {
            blockEntity.setStack(slot, stack);
        }
        // Otherwise, the redirect skips setStack(), effectively not costing any item
    }
}
