package carpet.tick;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TypedProfiler<T> {
	// AtomicLong used as mutable Long to save instantiation time
	public final Map<T, AtomicLong> phaseTimes = createPhaseMap();
	public final Map<T, AtomicInteger> phaseSwaps = createSwapMap();
	public T profilingPhase;
	public long lastSwapTime;

	public void swap(T phase) {
		T profilingPhase = this.profilingPhase;
		if (profilingPhase == phase) return;
		if (profilingPhase != null) {
			long phaseTime = (-lastSwapTime) + (lastSwapTime = System.nanoTime());
			phaseTimes.computeIfAbsent(profilingPhase, k -> new AtomicLong()).addAndGet(phaseTime);
			phaseSwaps.computeIfAbsent(profilingPhase, k -> new AtomicInteger()).incrementAndGet();
		}
		this.profilingPhase = phase;
	}

	public void clear() {
		phaseTimes.clear();
		phaseSwaps.clear();
		profilingPhase = null;
	}

	protected Map<T, AtomicLong> createPhaseMap() {
		return new HashMap<>();
	}
	protected Map<T, AtomicInteger> createSwapMap() {
		return new HashMap<>();
	}
}
