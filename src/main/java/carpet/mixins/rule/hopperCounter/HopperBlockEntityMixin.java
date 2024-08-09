package carpet.mixins.rule.hopperCounter;

import carpet.CarpetSettings;
import carpet.helpers.HopperCounter;
import carpet.utils.WoolTool;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.LootInventoryBlockEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin extends LootInventoryBlockEntity {
    @Shadow
    public abstract double getX();

    @Shadow
    public abstract double getY();

    @Shadow
    public abstract double getZ();

    @Shadow
    public abstract int getSize();

    @Shadow
    public abstract void setStack(int slot, ItemStack stack);

    @Inject(method = "pushItems()Z", at = @At("HEAD"), cancellable = true)
    private void countItem(CallbackInfoReturnable<Boolean> cir) {
        if (CarpetSettings.hopperCounters) {
            String counter;
            DyeColor woolColor = this.get_wool_pointing();
            counter = woolColor != null ? woolColor.getName() : null;
            if (counter != null) {
                for(int i = 0; i < this.getSize(); ++i) {
                    if (!this.getStack(i).isEmpty()) {
                        ItemStack itemStack = this.getStack(i);
                        HopperCounter.COUNTERS.get(counter).add(this.getWorld().getServer(), itemStack);
                        this.setStack(i, ItemStack.EMPTY);
                    }
                }
                cir.setReturnValue(true);
            }
        }
    }

    private DyeColor get_wool_pointing() {
        return WoolTool.getWoolColorAtPosition(
                getWorld(),
                new BlockPos(getX(), getY(), getZ()).offset(HopperBlock.getFacing(this.getBlockMetadata())));
    }
}
