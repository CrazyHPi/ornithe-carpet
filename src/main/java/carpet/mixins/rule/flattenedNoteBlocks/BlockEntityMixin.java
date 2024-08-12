package carpet.mixins.rule.flattenedNoteBlocks;

import carpet.CarpetSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.NoteBlockBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin {
	@Redirect(method = "markDirty", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/BlockEntity;getBlock()Lnet/minecraft/block/Block;"))
	public Block suppressComparatorUpdates(BlockEntity blockEntity) {
		if (CarpetSettings.flattenedNoteBlocks && blockEntity instanceof NoteBlockBlockEntity) return Blocks.AIR;
		return blockEntity.getBlock();
	}
}
