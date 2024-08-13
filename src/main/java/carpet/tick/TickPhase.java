package carpet.tick;

import carpet.utils.TranslationKeys;

public enum TickPhase {
	// From Carpet-RNY by Rainyaphthyl
	// Thank you Rainyaphtyl!!
	/////////////////////////////////////
	// The Game Phases are comparable! //
	// Do NOT change the order! /////////
	/////////////////////////////////////
	SERVER_TICK_COUNT(false, false, "TC"),
	ASYNC_TASKS(false, false, "AT"),
	CLIENT_TIME_SYNC(true, false, "CTS"),
	WEATHER_UPDATE(true, false, "WU"),
	HARDCODE_DIFFICULTY(true, false, "HDL"),
	SLEEP_AND_DAYTIME(true, false, "SDT"),
	MOB_SPAWNING(true, true, "MS"),
	CHUNK_UNLOAD(true, true, "CU"),
	WORLD_TIME_UPDATE(true, false, "WTC"),
	TILE_TICK(true, true, "TT"),
	PLAYER_LIGHT_CHECK(true, false, "PLC"),
	CHUNK_TICK(true, true, "CT"),
	CHUNK_MAP(true, true, "CM"),
	VILLAGE_TICK(true, true, "VT"),
	VILLAGE_SIEGE(true, false, "VS"),
	PORTAL_REMOVAL(true, false, "PR"),
	BLOCK_EVENT(true, true, "BE"),
	WORLD_IDLE_CHECK(true, false, "WIC"),
	DRAGON_FIGHT(true, true, "DF"),
	GLOBAL_ENTITY_UPDATE(true, true, "GE"),
	ENTITY_REMOVAL(true, true, "ER"),
	PLAYER_ENTITY_UPDATE(true, true, "PEU"),
	ENTITY_UPDATE(true, true, "EU"),
	BLOCK_ENTITY_UPDATE(true, true, "TE"),
	BLOCK_ENTITY_UNLOAD(true, true, "TER"),
	BLOCK_ENTITY_PENDING(true, true, "TEA"),
	ENTITY_TRACKING(true, false, "ET"),
	CONNECTION_UPDATE(false, true, "NU"),
	PLAYER_LIST_TICK(false, false, "PLT"),
	COMMAND_FUNCTION(false, false, "CF"),
	SERVER_AUTO_SAVE(false, false, "AS"),
	SP_VIEW_DISTANCE_ALT(false, false, "VDA", 1),
	SP_DIFFICULTY_ALT(false, false, "DA", 1),
	/////////////////////////////////////////
	// Things below should be at the first //
	/////////////////////////////////////////
	SERVER_INITIALIZE(false, false, "Init", 2),
	SP_SAVE_ON_PAUSE(false, false, "ASP", 1),
	SP_TASK_ON_PAUSE(false, false, "QTP", 1),
	/////////////////////////////////////////
	// Things above should be at the first //
	/////////////////////////////////////////
	SERVER_STOP(false, false, "Exit", 2);

	public final boolean dimensional;
	public final boolean profiled;
	public final String acronym;
	public final String translationKey;
	public final boolean singlePlayerOnly;
	public final boolean looped;
	public final TickPhase majorPhase;

	TickPhase(boolean dimensional, boolean profiled, String acronym) {
		this.dimensional = dimensional;
		this.profiled = profiled;
		this.acronym = acronym;
		this.translationKey = TranslationKeys.TICK_STAGE + acronym;
		this.singlePlayerOnly = false;
		this.looped = true;
		this.majorPhase = this;
	}

	TickPhase(boolean dimensional, boolean profiled, String acronym, int flags) {
		this.dimensional = dimensional;
		this.profiled = profiled;
		this.acronym = acronym;
		this.translationKey = TranslationKeys.TICK_STAGE + acronym;
		this.singlePlayerOnly = (flags & 1) != 0;
		this.looped = (flags & 2) == 0;
		this.majorPhase = this;
	}

	TickPhase(boolean dimensional, boolean profiled, String acronym, TickPhase majorPhase) {
		this.dimensional = dimensional;
		this.profiled = profiled;
		this.acronym = acronym;
		this.translationKey = TranslationKeys.TICK_STAGE + acronym;
		this.singlePlayerOnly = false;
		this.looped = true;
		this.majorPhase = majorPhase;
	}

	TickPhase(boolean dimensional, boolean profiled, String acronym, int flags, TickPhase majorPhase) {
		this.dimensional = dimensional;
		this.profiled = profiled;
		this.acronym = acronym;
		this.translationKey = TranslationKeys.TICK_STAGE + acronym;
		this.singlePlayerOnly = (flags & 1) != 0;
		this.looped = (flags & 2) == 0;
		this.majorPhase = majorPhase;
	}
}
