package carpet.mixins.rule.flattenedNoteBlocks;

import carpet.duck.NoteBlockBlockEntity$;
import carpet.CarpetSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.NoteBlockBlockEntity;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoteBlock.class)
public abstract class NoteBlockMixin {
	@Inject(method = "neighborChanged", at = @At(value = "FIELD",
		target = "Lnet/minecraft/block/entity/NoteBlockBlockEntity;powered:Z", opcode = 181 /* Opcodes.PUTFIELD */, shift = At.Shift.AFTER))
	public void poweredUpdateNeighborsAndObservers(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, CallbackInfo ci) {
		if (CarpetSettings.flattenedNoteBlocks) {
			world.updateNeighbors(pos, Blocks.NOTEBLOCK, true);
		}
	}

	@Inject(method = "use", at = @At("TAIL"))
	public void tuneUpdateNeighborsAndObservers(World world, BlockPos pos, BlockState state, PlayerEntity player, InteractionHand hand, Direction face, float dx, float dy, float dz, CallbackInfoReturnable<Boolean> cir) {
		if (CarpetSettings.flattenedNoteBlocks) {
			world.updateNeighbors(pos, Blocks.NOTEBLOCK, true);
		}
	}

	@Inject(method = "neighborChanged", at = @At("TAIL"))
	public void instrumentUpdateObservers(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, CallbackInfo ci) {
		if (CarpetSettings.flattenedNoteBlocks) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof NoteBlockBlockEntity) {
				int instrument = NoteBlockBlockEntity$.calculateInstrument(world.getBlockState(pos.down()));
				// Changing instrument sends observer updates in setInstrument
				((NoteBlockBlockEntity$) (NoteBlockBlockEntity) blockEntity).setInstrument(instrument);
			}
		}
	}
}
