package carpet.duck;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;

public interface NoteBlockBlockEntity$ {
    void setInstrument(int instrument);

    int getInstrument();

    static int calculateInstrument(BlockState blockState) {
        Material material = blockState.getMaterial();
        int i = 0;
        if (material == Material.STONE) {
            i = 1;
        }
        if (material == Material.SAND) {
            i = 2;
        }
        if (material == Material.GLASS) {
            i = 3;
        }
        if (material == Material.WOOD) {
            i = 4;
        }
        Block block = blockState.getBlock();
        if (block == Blocks.CLAY) {
            i = 5;
        }
        if (block == Blocks.GOLD_BLOCK) {
            i = 6;
        }
        if (block == Blocks.WOOL) {
            i = 7;
        }
        if (block == Blocks.PACKED_ICE) {
            i = 8;
        }
        if (block == Blocks.BONE_BLOCK) {
            i = 9;
        }
        return i;
    }
}
