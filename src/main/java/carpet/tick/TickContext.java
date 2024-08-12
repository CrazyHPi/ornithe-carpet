package carpet.tick;

import carpet.network.ServerNetworkHandler;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;

public class TickContext {
	// Cheap check fields for tick phase profiling and verbose profiling
	public static boolean profilingTickPhases = false;
	public static boolean profilingNeighborUpdates = false;
	public static boolean profilingTileTicks = false;
	public static boolean profilingEntities = false;
	public static boolean profilingBlockEntities = false;

	// Singleton
	private TickContext() {
		nanosPerTick = 50 * 1000 * 1000;
		warping = false;
		frozen = false;
		superHot = false;
		tickTimer();
	}
	public static final TickContext INSTANCE = new TickContext();

	// Tick rate context
	public long nanosPerTick;
	public boolean warping;
	// -1 = regularly running
	// 0  = frozen
	// >0 = freeze after that many ticks
	public int remainingTicks = 0;
	public boolean frozen;
	public boolean superHot;

	public void setTps(double tps) {
		nanosPerTick = (long) (1e9 / tps);
		accumulatedNanos = 0L;
		tickTimer();
		ServerNetworkHandler.updateTickRate((float) tps);
	}
	public void setFrozen(boolean frozen) {
		boolean flag = this.frozen != frozen;
		this.frozen = frozen;
		if (flag) ServerNetworkHandler.updateFrozenState(frozen);
	}
	public void flipFreezeState() {
		setFrozen(!frozen);
	}
	public void preTickFreezer() {
		setFrozen(remainingTicks == 0);
	}
	public void postTickFreezer() {
		if (remainingTicks > 0) -- remainingTicks;
		setFrozen(remainingTicks == 0);
	}


	private long accumulatedNanos = 0L;
	private int millisThisTick = 0;
	public int getMillisThisTick() {
		return millisThisTick;
	}
	public void tickTimer() {
		accumulatedNanos += nanosPerTick;
		millisThisTick = (int) (accumulatedNanos / 1000000L);
		accumulatedNanos %= 1000000L;
	}

	// Tick phase context
	// DIM_MAP[dimId + 1] = (0 = overworld, 1 = nether, 2 = end, 3 = canonical)
	private final int[] DIM_MAP = new int[] {1, 0, 2, 3};
	public static final int DIMENSION_INDEPENDENT_ID = 2;
	public int tickingDimension;
	public TickPhase currentPhase;

	public void swapTickingDimension(int dimensionId) {
		int newTickingDimension = DIM_MAP[dimensionId + 1];
		if (tickingDimension != newTickingDimension) {
			if (profilingTickPhases) tickPhaseProfilers[tickingDimension].swap(null);
			tickingDimension = newTickingDimension;
		}
	}
	public void swapTickPhase(TickPhase currentPhase) {
		this.currentPhase = currentPhase;
		if (profilingTickPhases) {
			if (currentPhase == null)
				tickPhaseProfilers[tickingDimension].swap(null);
			else if (currentPhase.profiled)
				tickPhaseProfilers[tickingDimension].swap(currentPhase);
			else
				tickPhaseProfilers[tickingDimension].swap(null);
		}
	}

	// Tick profile context;
	public final TypedProfiler<TickPhase>[] tickPhaseProfilers = new TickPhaseProfiler[]{
		new TickPhaseProfiler(), new TickPhaseProfiler(),
		new TickPhaseProfiler(), new TickPhaseProfiler()};
	public final TypedProfiler<Block> updateProfiler = new TypedProfiler<>();
	public final TypedProfiler<Block> tileTickProfiler = new TypedProfiler<>();
	public final TypedProfiler<Class<? extends BlockEntity>> blockEntityProfiler = new TypedProfiler<>();
	public final TypedProfiler<Class<? extends Entity>> entityProfiler = new TypedProfiler<>();
}
