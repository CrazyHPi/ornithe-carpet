package carpet.tick;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TickPhaseProfiler extends TypedProfiler<TickPhase> {
	// Flag meant for a cheap check before the more expensive operations
	public static boolean profiling = false;

	@Override
	protected Map<TickPhase, AtomicLong> createPhaseMap() {
		return new EnumMap<>(TickPhase.class);
	}

	@Override
	protected Map<TickPhase, AtomicInteger> createSwapMap() {
		return new EnumMap<>(TickPhase.class);
	}
}
