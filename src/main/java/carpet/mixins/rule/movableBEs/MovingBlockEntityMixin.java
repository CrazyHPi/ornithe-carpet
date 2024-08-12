package carpet.mixins.rule.movableBEs;

import carpet.duck.MovingBlockEntity$;
import carpet.CarpetSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityProvider;
import net.minecraft.block.entity.MovingBlockEntity;
import net.minecraft.block.entity.NoteBlockBlockEntity;
import net.minecraft.block.state.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Tickable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MovingBlockEntity.class)
public abstract class MovingBlockEntityMixin extends BlockEntity implements Tickable, MovingBlockEntity$ {
	@Unique
	private static final String CARRIED_BE_ID = "carriedTileEntity";

	@Shadow
	private BlockState movedState;

	@Unique
	private BlockEntity carriedBlockEntity;

	@Override
	public BlockEntity getCarriedBlockEntity() {
		return carriedBlockEntity;
	}

	@Override
	public void setCarriedBlockEntity(BlockEntity carriedBlockEntity) {
		this.carriedBlockEntity = carriedBlockEntity;
	}

	@Unique
	public void placeBlockWithEntity() {
		world.setBlockState(pos, movedState, 18);
		if (!world.isClient) {
			world.removeBlockEntity(pos);
			carriedBlockEntity.cancelRemoval();
			carriedBlockEntity.setWorld(world);
			world.setBlockEntity(pos, carriedBlockEntity);
		}
		world.updateNeighbors(pos, movedState.getBlock(), true);
		if (movedState.isAnalogSignalSource()) {
			world.updateNeighborComparators(pos, movedState.getBlock());
		}
		world.neighborChanged(pos, movedState.getBlock(), pos);
	}

	@Unique
	public boolean carriesValidEntity() {
		return (CarpetSettings.movableBlockEntities && carriedBlockEntity != null) ||
			(CarpetSettings.flattenedNoteBlocks && carriedBlockEntity instanceof NoteBlockBlockEntity);
	}

	@Inject(method = "tick", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/block/entity/MovingBlockEntity;markRemoved()V", shift = At.Shift.AFTER)
	, cancellable = true)
	public void injectActivePlaceCarriedBE(CallbackInfo ci) {
		if (this.carriesValidEntity() && world.getBlockState(pos).getBlock() == Blocks.MOVING_BLOCK) {
			this.placeBlockWithEntity();
			ci.cancel();
		}
	}

	@Inject(method = "finish", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/block/entity/MovingBlockEntity;markRemoved()V", shift = At.Shift.AFTER)
	, cancellable = true)
	public void injectPassivePlaceCarriedBE(CallbackInfo ci) {
		if (this.carriesValidEntity()) {
			if (world.getBlockState(pos).getBlock() == Blocks.MOVING_BLOCK) {
				// Removals such as sticky piston dropping
				this.placeBlockWithEntity();
			} else if (world.getBlockState(pos).getBlock() == Blocks.AIR) {
				// Removals such as TNT explosion - drop content of containers
				this.placeBlockWithEntity();
				world.removeBlock(pos);
			}
			ci.cancel();
		}
	}

	@Inject(method = "readNbt", at = @At("TAIL"))
	public void injectReadCarriedBE(NbtCompound nbt, CallbackInfo ci) {
		Block movedBlock = movedState.getBlock();
		if ((CarpetSettings.movableBlockEntities && movedBlock instanceof BlockEntityProvider) ||
			(CarpetSettings.flattenedNoteBlocks && movedBlock instanceof NoteBlock)) {
			carriedBlockEntity = ((BlockEntityProvider) movedBlock).createBlockEntity(
				this.world, movedBlock.getMetadataFromState(movedState));
			if (nbt.contains(CARRIED_BE_ID)) {
				NbtCompound carriedNbt = nbt.getCompound(CARRIED_BE_ID);
				carriedBlockEntity.readNbt(carriedNbt);
			}
		}
	}

	@Inject(method = "writeNbt", at = @At("TAIL"))
	public void injectWriteCarriedBE(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
		if (this.carriesValidEntity()) {
			NbtCompound carriedNbt = carriedBlockEntity.toNbt();
			nbt.put(CARRIED_BE_ID, carriedNbt);
		}
	}
}
