package carpet.mixins.rule.movableBEs;

import carpet.duck.MovingBlockEntity$;
import carpet.CarpetSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBaseBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MovingBlockEntity;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PistonBaseBlock.class)
public abstract class PistonBaseBlockMixin {
	@Unique
	private static boolean isPushableEntityBlock(Block block) {
		if (CarpetSettings.movableBlockEntities) {
			return block != Blocks.ENDER_CHEST && block != Blocks.ENCHANTING_TABLE && block != Blocks.END_GATEWAY
				&& block != Blocks.END_PORTAL && block != Blocks.MOB_SPAWNER && block != Blocks.MOVING_BLOCK;
		} else if (CarpetSettings.flattenedNoteBlocks) {
			return block == Blocks.NOTEBLOCK;
		} else return false;
	}

	@WrapOperation(method = "canMoveBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;hasBlockEntity()Z"))
	private static boolean redirectHasBlockEntity(Block instance, Operation<Boolean> original) {
		if (CarpetSettings.movableBlockEntities || CarpetSettings.flattenedNoteBlocks) {
			return original.call(instance) && !isPushableEntityBlock(instance);
		} else {
			return original.call(instance);
		}
	}

	@WrapOperation(method = "move", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/BlockState;",
		ordinal = 2))
	public BlockState recordBlockEntity(World world, BlockPos pos, Operation<BlockState> original,
		@Share("ornitheCarpet$blockEntity") LocalRef<BlockEntity> blockEntityRef) {
		if (CarpetSettings.movableBlockEntities || CarpetSettings.flattenedNoteBlocks) {
			blockEntityRef.set(world.getBlockEntity(pos));
			world.removeBlockEntity(pos);
		}
		else blockEntityRef.set(null);
		return original.call(world, pos);
	}

	@WrapOperation(method = "move", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/block/MovingBlock;createMovingBlockEntity(Lnet/minecraft/block/state/BlockState;Lnet/minecraft/util/math/Direction;ZZ)Lnet/minecraft/block/entity/BlockEntity;",
		ordinal = 0))
	public BlockEntity createMovingBlockEntity(
		BlockState movedState, Direction facing, boolean extending, boolean source,
		Operation<BlockEntity> original,
		@Share("ornitheCarpet$blockEntity") LocalRef<BlockEntity> blockEntityRef) {
		BlockEntity movingBlockEntity = original.call(movedState, facing, extending, source);
		BlockEntity carriedBlockEntity = blockEntityRef.get();
		if (carriedBlockEntity != null && movingBlockEntity instanceof MovingBlockEntity)
			// No need to check the carpet rules here
			// If the rules are off, blockEntityToMove would be null here
			((MovingBlockEntity$) movingBlockEntity).setCarriedBlockEntity(carriedBlockEntity);
		return movingBlockEntity;
	}
}
