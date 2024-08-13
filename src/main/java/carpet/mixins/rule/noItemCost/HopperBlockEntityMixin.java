package carpet.mixins.rule.noItemCost;

import carpet.CarpetSettings;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin {
    @Redirect(method = "pushItems()Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/HopperBlockEntity;removeStack(II)Lnet/minecraft/item/ItemStack;"))
    public ItemStack redirectNoItemCost(HopperBlockEntity self, int slot, int amount) {
        if ("none".equals(CarpetSettings.hopperNoItemCost))
            return self.removeStack(slot, amount);
        boolean noCost = false;
        if ("all".equals(CarpetSettings.hopperNoItemCost)) noCost = true;
        else {
            World world = self.getWorld();
            BlockPos pos = self.getPos();
            noCost = world.getBlockState(pos.up()).getBlock() == Blocks.WOOL;
        }
        if (noCost) {
            return self.getStack(slot).copy().split(amount);
        }
        return self.removeStack(slot, amount);
    }
}
