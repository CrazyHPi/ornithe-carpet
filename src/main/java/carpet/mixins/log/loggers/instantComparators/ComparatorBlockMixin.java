package carpet.mixins.log.loggers.instantComparators;

import carpet.fakes.ComparatorBlockEntityF;
import carpet.log.framework.LoggerRegistry;
import carpet.log.loggers.instantComparators.InstantComparators;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ComparatorBlockEntity;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(ComparatorBlock.class)
public abstract class ComparatorBlockMixin {
    @Shadow
    protected abstract int calculateOutputSignal(World world, BlockPos pos, BlockState state);

    @Inject(
            method = "checkOutputState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/ComparatorBlock;shouldPrioritize(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/BlockState;)Z"
            )
    )
    private void logSignal(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (LoggerRegistry.__instantComparators) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ComparatorBlockEntity) {
                ComparatorBlockEntity comparator = (ComparatorBlockEntity) blockEntity;
                int blockSignal = this.calculateOutputSignal(world, pos, state);
                int blockEntitySignal = comparator.getOutputSignal();

                int index = (int) Math.floorMod(world.getTime() + 2, 3);
                ((ComparatorBlockEntityF) comparator).setScheduledOutputSignal(index, blockSignal);
                ((ComparatorBlockEntityF) comparator).setBuggy(index, blockSignal == blockEntitySignal);
            } else {
                InstantComparators.onNoTileEntity(world, pos);
            }
        }
    }

    // RETURN ordinal=0 basically means (!world.willTickThisTick(pos, this)) evaluate as false
    @Inject(method = "checkOutputState", at = @At(value = "RETURN", ordinal = 0))
    private void logFinalSignal(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (LoggerRegistry.__instantComparators) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ComparatorBlockEntity) {
                ComparatorBlockEntity comparator = (ComparatorBlockEntity) blockEntity;
                int index = (int) Math.floorMod(world.getTime() + 2, 3);
                ((ComparatorBlockEntityF) comparator).setScheduledOutputSignal(index, calculateOutputSignal(world, pos, state));
            }
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void logOnUpdate(World world, BlockPos pos, BlockState state, Random random, CallbackInfo ci) {
        if (LoggerRegistry.__instantComparators) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ComparatorBlockEntity) {
                ComparatorBlockEntity comparator = (ComparatorBlockEntity) blockEntity;
                int index = (int) Math.floorMod(world.getTime(), 3);
                // output signal 0 is generally considered to just be a too fast pulse for a comparator, rather
                // than an instant comparator
                if (comparator.getOutputSignal() != ((ComparatorBlockEntityF) comparator).getScheduledOutputSignal()[index] && comparator.getOutputSignal() != 0) {
                    InstantComparators.onInstantComparator(world, pos, ((ComparatorBlockEntityF) comparator).getBuggy()[index]);
                }
            }
        }
    }
}
