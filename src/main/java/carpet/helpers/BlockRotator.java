package carpet.helpers;

import carpet.CarpetSettings;
import net.minecraft.block.*;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.World;

public class BlockRotator {
    public static boolean flipBlockWithCactus(World worldIn, BlockPos pos, BlockState state, PlayerEntity playerIn, InteractionHand hand, Direction facing, float hitX, float hitY, float hitZ) {
        if (!playerIn.abilities.canModifyWorld || !CarpetSettings.flippinCactus || !playerHoldsCactusMainHand(playerIn)) {
            return false;
        }
        return flipBlock(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    public static boolean flipBlock(World worldIn, BlockPos pos, BlockState state, PlayerEntity playerIn, InteractionHand hand, Direction facing, float hitX, float hitY, float hitZ) {
        Block block = state.getBlock();
        if ((block instanceof GlazedTerracottaBlock) || (block instanceof DiodeBlock) || (block instanceof AbstractRailBlock) ||
                (block instanceof TrapdoorBlock) || (block instanceof LeverBlock) || (block instanceof FenceGateBlock)) {
            worldIn.setBlockState(pos, block.rotate(state, BlockRotation.CLOCKWISE_90), 130);
        } else if ((block instanceof ObserverBlock) || (block instanceof EndRodBlock)) {
            worldIn.setBlockState(pos, state.set(FacingBlock.FACING, state.get(FacingBlock.FACING).getOpposite()), 130);
        } else if (block instanceof DispenserBlock) {
            worldIn.setBlockState(pos, state.set(DispenserBlock.FACING, state.get(DispenserBlock.FACING).getOpposite()), 130);
        } else if (block instanceof PistonBaseBlock) {
            if (!(state.get(PistonBaseBlock.EXTENDED)))
                worldIn.setBlockState(pos, state.set(FacingBlock.FACING, state.get(FacingBlock.FACING).getOpposite()), 130);
        } else if (block instanceof SlabBlock) {
            if (!((SlabBlock) block).isDouble()) {
                if (state.get(SlabBlock.HALF) == SlabBlock.Half.TOP) {
                    worldIn.setBlockState(pos, state.set(SlabBlock.HALF, SlabBlock.Half.BOTTOM), 130);
                } else {
                    worldIn.setBlockState(pos, state.set(SlabBlock.HALF, SlabBlock.Half.TOP), 130);
                }
            }
        } else if (block instanceof HopperBlock) {
            if (state.get(HopperBlock.FACING) != Direction.DOWN) {
                worldIn.setBlockState(pos, state.set(HopperBlock.FACING, state.get(HopperBlock.FACING).clockwiseY()), 130);
            }
        } else if (block instanceof StairsBlock) {
            if ((facing == Direction.UP && hitY == 1.0f) || (facing == Direction.DOWN && hitY == 0.0f)) {
                if (state.get(StairsBlock.HALF) == StairsBlock.Half.TOP) {
                    worldIn.setBlockState(pos, state.set(StairsBlock.HALF, StairsBlock.Half.BOTTOM), 130);
                } else {
                    worldIn.setBlockState(pos, state.set(StairsBlock.HALF, StairsBlock.Half.TOP), 130);
                }
            } else {
                boolean turn_right;
                if (facing == Direction.NORTH) {
                    turn_right = (hitX <= 0.5);
                } else if (facing == Direction.SOUTH) {
                    turn_right = !(hitX <= 0.5);
                } else if (facing == Direction.EAST) {
                    turn_right = (hitZ <= 0.5);
                } else if (facing == Direction.WEST) {
                    turn_right = !(hitZ <= 0.5);
                } else {
                    return false;
                }
                if (turn_right) {
                    worldIn.setBlockState(pos, block.rotate(state, BlockRotation.COUNTERCLOCKWISE_90), 130);
                } else {
                    worldIn.setBlockState(pos, block.rotate(state, BlockRotation.CLOCKWISE_90), 130);
                }
            }
        } else {
            return false;
        }
        worldIn.notifyRegionChanged(pos, pos);
        return true;
    }

    private static boolean playerHoldsCactusMainHand(PlayerEntity playerIn) {
        return (!playerIn.getItemInMainHand().isEmpty()
                && playerIn.getItemInMainHand().getItem() instanceof BlockItem
                && ((BlockItem) (playerIn.getItemInMainHand().getItem())).getBlock() == Blocks.CACTUS);
    }

    // used for flip block rotation when placing block with catus offhand
    public static boolean playerHoldsCactusOffHand(Entity entity) {
        if (CarpetSettings.flippinCactus && (entity instanceof PlayerEntity)) {
            PlayerEntity player = (PlayerEntity) entity;
            return (!player.getItemInOffHand().isEmpty()
                    && player.getItemInOffHand().getItem() instanceof BlockItem
                    && ((BlockItem) (player.getItemInOffHand().getItem())).getBlock() == Blocks.CACTUS);
        }
        return false;
    }
}
