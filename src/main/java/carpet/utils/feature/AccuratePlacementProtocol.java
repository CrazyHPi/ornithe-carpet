package carpet.utils.feature;

import net.minecraft.block.*;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class AccuratePlacementProtocol {
	private AccuratePlacementProtocol() { throw new AssertionError(); }

	public static <T extends Comparable<T>> Property<T> firstPropertyOfType(BlockState state, Class<T> type) {
		for (Property<?> property : state.properties()) {
			if (property.getType() == type) return (Property<T>) property;
		}
		return null;
	}

	public static <T extends Comparable<T>> Optional<T> firstValueOfType(BlockState state, Class<T> type) {
		Property<T> property = firstPropertyOfType(state, type);
		if (property == null) return Optional.empty();
		return Optional.of(state.get(property));
	}

	public static Vec3d encodeAccuratePlacementProtocol(BlockPos pos, BlockState state, Vec3d hitVecIn)
	{
		double x = hitVecIn.x;
		double y = hitVecIn.y;
		double z = hitVecIn.z;
		Block block = state.getBlock();
		Optional<Direction> facingOptional = firstValueOfType(state, Direction.class);
		if (facingOptional.isPresent()) {
			x = facingOptional.get().ordinal() + 2 + pos.getX();
		}
		if (block instanceof RepeaterBlock) {
			x += ((state.get(RepeaterBlock.DELAY)) - 1) * 10;
		} else if (block instanceof TrapdoorBlock && state.get(TrapdoorBlock.HALF) == TrapdoorBlock.Half.TOP) {
			x += 10;
		} else if (block instanceof ComparatorBlock && state.get(ComparatorBlock.MODE) == ComparatorBlock.Mode.SUBTRACT) {
			x += 10;
		} else if (block instanceof StairsBlock && state.get(StairsBlock.HALF) == StairsBlock.Half.TOP) {
			x += 10;
		}
		return new Vec3d(x, y, z);
	}

	public static BlockState decodeAccuratePlacementProtocol(BlockState state, float dx) {
		if (dx <= 1) return null;
		int extra = (int) dx - 2;
		Direction direction = Direction.byId(extra % 10);
		extra = extra / 10;
		Property<Direction> property = firstPropertyOfType(state, Direction.class);
		if (property != null) {
			state = state.set(property, direction);
		}
		Block block = state.getBlock();
		if (block instanceof RepeaterBlock) {
			state = state.set(RepeaterBlock.DELAY, extra + 1);
		} else if (block instanceof TrapdoorBlock) {
			state = state.set(TrapdoorBlock.HALF, extra > 0 ? TrapdoorBlock.Half.TOP : TrapdoorBlock.Half.BOTTOM);
		} else if (block instanceof ComparatorBlock) {
			state = state.set(ComparatorBlock.MODE, extra > 0 ? ComparatorBlock.Mode.SUBTRACT : ComparatorBlock.Mode.COMPARE);
		} else if (block instanceof StairsBlock) {
			state = state.set(StairsBlock.HALF, extra > 0 ? StairsBlock.Half.TOP : StairsBlock.Half.BOTTOM);
		}
		return state;
	}
}
