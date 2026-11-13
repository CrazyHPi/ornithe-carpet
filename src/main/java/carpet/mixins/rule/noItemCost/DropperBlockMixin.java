package carpet.mixins.rule.noItemCost;

import carpet.CarpetSettings;
import net.minecraft.block.Blocks;
import net.minecraft.block.DropperBlock;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DropperBlock.class)
public abstract class DropperBlockMixin {
    @Redirect(method = "dispense", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/DispenserBlockEntity;setItem(ILnet/minecraft/item/ItemStack;)V"))
    public void setStackNoItemCost(DispenserBlockEntity blockEntity, int slot, ItemStack stack) {
        if ("off".equals(CarpetSettings.dropperNoItemCost)) {
            blockEntity.setItem(slot, stack);
        } else if ("wool".equals(CarpetSettings.dropperNoItemCost) &&
                blockEntity.getWorld().getBlockState(blockEntity.getPos().down()).getBlock() == Blocks.WOOL) {
            blockEntity.setItem(slot, stack);
        }
        // Otherwise, the redirect skips setItem(), effectively not costing any item
    }
}
