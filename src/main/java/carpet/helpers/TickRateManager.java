package carpet.helpers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;

public class TickRateManager {
	public static final int PLAYER_GRACE = 2;
	protected float tickrate = 20.0f;
	protected long mspt = 50L;
	protected int playerActivityTimeout = 0;
	protected boolean runGameElements = true;
	// deep freeze is only used serverside
	protected boolean deepFreeze = false;
	protected boolean isGamePaused = false;
	protected boolean isSuperHot = false;

	public void setTickRate(float rate) {
		tickrate = rate;
		long msptt = (long) (1000.0 / tickrate);
		if (msptt <= 0L) {
			msptt = 1L;
			tickrate = 1000.0f;
		}
		mspt = msptt;
	}

	public float tickrate() {
		return tickrate;
	}

	/**
	 * @return Whether or not the game is in a frozen state.
	 * You should normally use {@link #runGameElements} instead,
	 * since that one accounts for tick steps and superhot
	 */
	public boolean gameIsPaused() {
		return isGamePaused;
	}

	public long mspt() {
		return mspt;
	}

	public boolean runsNormally() {
		return runGameElements;
	}

	public boolean isSuperHot() {
		return isSuperHot;
	}

	public void setSuperHot(boolean isSuperHot) {
		this.isSuperHot = isSuperHot;
	}

	public int getPlayerActiveTimeout() {
		return playerActivityTimeout;
	}

	public void setPlayerActiveTimeout(int timeout) {
		playerActivityTimeout = timeout;
	}

	public void setFrozenState(boolean isPaused, boolean isDeepFreeze) {
		isGamePaused = isPaused;
		deepFreeze = isPaused && isDeepFreeze;
	}

	public void tick() {
		if (playerActivityTimeout > 0) {
			playerActivityTimeout--;
		}
		if (isGamePaused) {
			runGameElements = playerActivityTimeout >= PLAYER_GRACE;
		} else if (isSuperHot) {
			runGameElements = playerActivityTimeout > 0;
		} else {
			runGameElements = true;
		}
	}

	public boolean shouldEntityTick(Entity e) {
		// client
		return (runsNormally() || (e instanceof PlayerEntity) || isSuperHot() && e.getControllingPassenger() instanceof PlayerEntity);
	}
}
