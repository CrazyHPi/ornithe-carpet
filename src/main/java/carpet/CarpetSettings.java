package carpet;

import carpet.api.settings.*;
import carpet.settings.validation.*;
import carpet.utils.Messenger;
import carpet.utils.Translations;
import net.minecraft.server.command.source.CommandSource;
import org.jetbrains.annotations.Nullable;

import static carpet.api.settings.RuleCategory.*;

public class CarpetSettings {
	/*
		  ____
		 / ___|  ___   _ __   ___
		| |     / _ \ | '__| / _ \
		| |___ | (_) || |   |  __/
		 \____| \___/ |_|    \___|
	 */
	// CORE - Central settings important for the carpet mod framework
	@Rule(
			desc = "Sets the language for Carpet",
			categories = RuleCategory.FEATURE,
			options = {"en_us"},
			validators = Validators.LanguageValidator.class
	)
	public static String language = "en_us";

	@Rule(
			desc = "Carpet command permission level",
			categories = RuleCategory.CREATIVE,
			validators = Validators.CarpetPermissionLevel.class,
			options = {"ops", "2", "4"}
	)
	public static String carpetCommandPermissionLevel = "ops";

	/*
          _____         _
         |_   _|       | |
           | |   _ __  | |_  ___
           | |  | '_ \ | __|/ __|
          _| |_ | | | || |_ \__ \
         |_____||_| |_| \__||___/
     */
	// INTS - This sections contains all the integer fields
	@Rule(
		desc = "Range for block events to be synced to client",
		options = {"4", "64", "256"},
		categories = RuleCategory.OPTIMIZATION,
		strict = false,
		validators = Validators.PositiveIn10Bits.class
	)
	public static int blockEventRange = 64;

	@Rule(
		desc = "Custom limit of changed blocks for /fill and /clone",
		options = {"32768", "114514", "1919810", "2147483647"},
		categories = RuleCategory.CREATIVE,
		strict = false,
		validators = Validators.NonNegativeNumber.class
	)
	public static int fillLimit = 32768;

	@Rule(
		desc = "Limit for entity collisions per entity per tick, 0 = no limit",
		options = {"0", "20", "100"}, categories = RuleCategory.OPTIMIZATION,
		strict = false, validators = Validators.NonNegativeNumber.class
	)
	public static int maxEntityCollisions = 0;

	@Rule(
		desc = "Percentage of online players required to be sleeping to skip night",
		options = {"100", "0", "20"},
		categories = RuleCategory.FEATURE,
		strict = false,
		validators = Validators.Percentage.class
	)
	public static int playersSleepingPercentage = 100;

	@Rule(
		desc = "Amount of delay ticks to use a nether portal in creative",
		options = {"1", "40", "80", "72000"},
		categories = RuleCategory.CREATIVE,
		strict = false,
		validators = Validators.OneHourMaxDelayLimit.class
	)
	public static int portalCreativeDelay = 1;

	@Rule(
		desc = "Amount of delay ticks to use a nether portal in survival",
		options = {"1", "40", "80", "72000"},
		categories = RuleCategory.SURVIVAL,
		strict = false,
		validators = Validators.OneHourMaxDelayLimit.class
	)
	public static int portalSurvivalDelay = 80;

	@Rule(
		desc = "Customizable piston push limit",
		options = {"10", "12", "14", "100"},
		categories = RuleCategory.CREATIVE,
		strict = false,
		validators = Validators.PositiveIn10Bits.class
	)
	public static int pushLimit = 12;

	@Rule(
		desc = "Customizable powered rail power range",
		options = {"9", "15", "30"},
		categories = RuleCategory.CREATIVE,
		strict = false,
		validators = Validators.PositiveIn10Bits.class
	)
	public static int railPowerLimit = 9;

	@Rule(
		desc = "Customizable maximum # of tile ticks ran per tick",
		options = {"1000", "65536", "2147483647"},
		categories = RuleCategory.CREATIVE,
		strict = false,
		validators = Validators.NonNegativeNumber.class
	)
	public static int tileTickLimit = 65536;

	/*
		  ____  _  _               _
		 / ___|| |(_)  ___  _ __  | |_
		| |    | || | / _ \| '_ \ | __|
		| |___ | || ||  __/| | | || |_
		 \____||_||_| \___||_| |_| \__|
	 */
	// CLIENT - This section contains all the client-related settings

	@Rule(
		desc = "Creative No Clip",
		extra = {
			"On servers it needs to be set on both ",
			"client and server to function properly.",
			"Has no effect when set on the server only",
			"Can allow to phase through walls",
			"if only set on the carpet client side",
			"but requires some trapdoor magic to",
			"allow the player to enter blocks"
		},
		categories = {RuleCategory.CREATIVE, RuleCategory.CLIENT}
	)
	public static boolean creativeNoClip = false;

	@Rule(
		desc = "Make client animations smooth for /tick rate and /tick freeze",
		categories = RuleCategory.CLIENT
	)
	public static boolean smoothClientAnimations = false;

	/*
		 _____  _   _  _____
		|_   _|| \ | ||_   _|
		  | |  |  \| |  | |
		  | |  | |\  |  | |
		  |_|  |_| \_|  |_|
	 */
	// TNT - This section contains all the TNT related settings

	@Rule(desc = "Explosions won't destroy blocks",
		categories = {RuleCategory.CREATIVE, RuleCategory.TNT})
	public static boolean explosionNoBlockDamage = false;

	@Rule(desc = "TNT doesn't update when placed against a power source",
		categories = {RuleCategory.CREATIVE, RuleCategory.TNT})
	public static boolean tntDoNotUpdate = false;

	@Rule(desc = "Fix random angle of TNT for debugging; unit in radians, <0 = vanilla behavior",
		categories = {RuleCategory.CREATIVE, RuleCategory.TNT},
		options = {"-1.0"}, strict = false)
	public static double tntFixedRandomAngle = -1.0;

	@Rule(desc = "Fix random range of TNT to 0.7+0.6*setting, <0 = vanilla behavior",
		categories = {RuleCategory.CREATIVE, RuleCategory.TNT},
		options = {"-1.0"}, strict = false,
		validators = Validators.OptionalProbability.class)
	public static float tntFixedRandomRange = -1.0f;

	@Rule(desc = "Changes default tnt fuse.",
		categories = {RuleCategory.CREATIVE, RuleCategory.TNT},
		validators = Validators.NonNegativeNumber.class,
		options = {"70", "80", "100"},
		strict = false)
	public static int tntFuseLength = 80;

	@Rule(desc = "TNT no longer receives initial momentum", categories = {RuleCategory.TNT, RuleCategory.YEET})
	public static boolean yeetTntInitialMotion = false;

	/*
		 ____                        _         _    _
		|  _ \   ___   _ __   _   _ | |  __ _ | |_ (_)  ___   _ __
		| |_) | / _ \ | '_ \ | | | || | / _` || __|| | / _ \ | '_ \
		|  __/ | (_) || |_) || |_| || || (_| || |_ | || (_) || | | |
		|_|     \___/ | .__/  \__,_||_| \__,_| \__||_| \___/ |_| |_|
					  |_|
	 */
	// POPULATION - This section contains population/population suppression/async exploit
	//              related rules
	@Rule(desc = "Updating a beacon with redstone power sends an async NC and PP update each",
		categories = {RuleCategory.CREATIVE, RuleCategory.POPULATION})
	public static boolean asyncBeaconUpdate = false;

	@Rule(desc = "Async world modification no longer mess up internal states of player chunk map",
		categories = {RuleCategory.CREATIVE, RuleCategory.POPULATION})
	public static boolean asyncPacketSyncing = false;

	@Rule(desc = "Chunk map no longer throws a possible CME with an async line running - infamous 8001gt crash",
		categories = {RuleCategory.CREATIVE, RuleCategory.POPULATION, RuleCategory.TWEAK},
		validators = ChunkMapCrashFixModifier.class)
	public static boolean fixAsyncChunkMapCrash = false;

	@Rule(desc = "Instant fall, the global flag turned on by suppression any part of a population",
		categories = {RuleCategory.CREATIVE, RuleCategory.POPULATION},
		validators = IFModifier.class)
	public static boolean instantFall = false;

	@Rule(desc = "Instant tile ticks, the dimensional flag turned on by suppressing a populating liquid pocket",
		categories = {RuleCategory.CREATIVE, RuleCategory.POPULATION},
		validators = ITTModifier.class,
		options = {"none", "overworld_false", "overworld_true", "nether_false", "nether_true",
			"end_false", "end_true"})
	public static String instantTileTicks = "none";

	@Rule(desc = "Redstone power, the global flag turned off by suppressing a population caused by an RS dust power check",
		categories = {RuleCategory.CREATIVE, RuleCategory.POPULATION},
		validators = RPModifier.class)
	public static boolean redstonePower = true;

	/*
		  ____                     _    _
		 / ___| _ __   ___   __ _ | |_ (_)__   __  ___
		| |    | '__| / _ \ / _` || __|| |\ \ / / / _ \
		| |___ | |   |  __/| (_| || |_ | | \ V / |  __/
		 \____||_|    \___| \__,_| \__||_|  \_/   \___|
	 */
	// CREATIVE - This sections contains rule solely for the purpose of creative mode testing and should never be on
	//            for survival servers
	@Rule(desc = "Controls the circumstances in which dispensers don't consume items when firing",
		categories = RuleCategory.CREATIVE,
		options = {"off", "wool", "all"})
	public static String dispenserNoItemCost = "off";

	@Rule(desc = "Controls the circumstances in which droppers don't consume items when firing",
		categories = RuleCategory.CREATIVE,
		options = {"off", "wool", "all"})
	public static String dropperNoItemCost = "off";

	@Rule(desc = "/fill and /clone has setBlockState flags 18 and onAdded/onRemoved suppressed",
		categories = RuleCategory.CREATIVE)
	public static boolean fillUpdates = true;

	@Rule(desc = "Controls the circumstances in which hoppers don't consume items when transferring out",
		categories = RuleCategory.CREATIVE,
		options = {"off", "wool", "all"})
	public static String hopperNoItemCost = "off";

	@Rule(desc = "Emerald ore receiving power throws an exception on update",
		categories = RuleCategory.CREATIVE)
	public static boolean oreUpdateSuppressor = false;

	/*
		 _____                         _
		|_   _|__      __  ___   __ _ | | __
		  | |  \ \ /\ / / / _ \ / _` || |/ /
		  | |   \ V  V / |  __/| (_| ||   <
		  |_|    \_/\_/   \___| \__,_||_|\_\
	 */
	// TWEAK - This section contains fixes to unintuitive/inconvenient vanilla features
	//         that may be somewhat survival-oriented
	@Rule(desc = "Flexible/accurate block placement places blocks in a precise direction", categories = RuleCategory.FEATURE)
	public static boolean accurateBlockPlacement = false;

	@Rule(desc = "Clicking on a cake always eats a slice",
		categories = RuleCategory.TWEAK)
	public static boolean cakeAlwaysEat = false;

	@Rule(desc = "Whether observers pulse once when placed by a player",
		categories = {RuleCategory.SURVIVAL, RuleCategory.TWEAK})
	public static boolean observerInitialPulse = true;

	//#if MC>=11200
	@Rule(desc = "Parrots don't get of your shoulders until you receive proper damage",
		categories = {RuleCategory.SURVIVAL, RuleCategory.TWEAK})
	public static boolean persistentParrots = false;
	//#endif

	@Rule(desc = "Allow placing of pumpkins and fence gates mid-air",
		categories = {RuleCategory.SURVIVAL, RuleCategory.TWEAK})
	public static boolean relaxedBlockPlacement = false;

	/*
		__   __             _
		\ \ / /  ___   ___ | |_
		 \ V /  / _ \ / _ \| __|
		  | |  |  __/|  __/| |_
		  |_|   \___| \___| \__|
	 */
	// YEET - Radical changes often totally disabling certain important features from the game,
	//        oriented for debugging purposes that should only be set to a non-default value
	//        for short intervals

	@Rule(desc = "Rule controlling quasi-connectivity", categories = RuleCategory.YEET)
	public static boolean quasiConnectivity = true;

    @Rule(desc = "Prevents all onRemoved() calls", categories = RuleCategory.YEET)
    public static boolean yeetRemovalUpdates = false;

	@Rule(desc = "Prevents all onAdded() calls", categories = RuleCategory.YEET)
	public static boolean yeetInitialUpdates = false;

	@Rule(desc = "Prevents all comparator updates", categories = RuleCategory.YEET)
	public static boolean yeetComparatorUpdates = false;

	@Rule(desc = "Prevents all neighbor updates", categories = RuleCategory.YEET)
	public static boolean yeetNeighborUpdates = false;

	@Rule(desc = "Prevents all observer updates", categories = RuleCategory.YEET)
	public static boolean yeetObserverUpdates = false;



	/*
		 _____               _
		|  ___|  ___   __ _ | |_  _   _  _ __   ___
		| |_    / _ \ / _` || __|| | | || '__| / _ \
		|  _|  |  __/| (_| || |_ | |_| || |   |  __/
		|_|     \___| \__,_| \__| \__,_||_|    \___|
	 */
	// FEATURE - This section contains added non-vanilla features that does not belong to a preceding category

	@Rule(desc = "Note blocks have exact 1.13 behavior", categories = RuleCategory.FEATURE)
	public static boolean flattenedNoteBlocks = false;

	@Rule(desc = "Block entities can be pushed or pulled by pistons", categories = RuleCategory.FEATURE)
	public static boolean movableBlockEntities = false;

	@Rule(desc = "Players absorb XP instantly, without delay", categories = RuleCategory.FEATURE)
	public static boolean xpNoCooldown = false;

    @Rule(desc = "Gbhs sgnf sadsgras fhskdpri!!!", categories = EXPERIMENTAL)
    public static boolean superSecretSetting = false;

    @Rule(desc = "Fixes the elytra check similar to 1.15 where the player do not have to fall to deploy elytra anymore.", categories = BUGFIX)
    public static boolean elytraCheckFix;
    @Rule(desc = "Prevents players from rubberbanding when moving too fast", categories = SURVIVAL)
    public static boolean antiCheatSpeed = false;
    @Rule(
            desc = "hoppers pointing to wool will count items passing through them",
            extra = {
                    "Enables /counter command, and actions while placing red and green carpets on wool blocks",
                    "Use /counter <color?> reset to reset the counter, and /counter <color?> to query",
                    "In survival, place green carpet on same color wool to query, red to reset the counters",
                    "Counters are global and shared between players, 16 channels available",
                    "Items counted are destroyed, count up to one stack per tick per hopper"
            },
            categories = {COMMAND, CREATIVE, FEATURE}
    )
    public static boolean hopperCounters = false;
    @Rule(desc = "Items thrown into a cactus will count items that are destroyed in them.", categories = {CREATIVE})
    public static boolean cactusCounter = false;
    @Rule(
            desc = "HUD update interval",
            categories = FEATURE,
            options = {"1", "5", "20", "100"},
            validators = HUDUpdateIntervalValidator.class
    )
    public static int hudUpdateInterval = 20;
    @Rule(desc = "Enables /log command to monitor events via chat and overlays", categories = COMMAND)
    public static String commandLog = "true";

    // carpet command related
    private static class LanguageValidator extends Validator<String> {
        @Override
        public String validate(CommandSource source, CarpetRule<String> currentRule, String newValue, String string) {
            if (!Translations.isValidLanguage(newValue)) {
                Messenger.m(source, "r " + newValue + " is not a valid language");
                return null;
            }
            language = newValue;
            Translations.updateLanguage();
            return newValue;
        }
    }

    private static class CarpetPermissionLevel extends Validator<String> {
        @Override
        public String validate(CommandSource source, CarpetRule<String> currentRule, String newValue, String string) {
            if (source == null || source.canUseCommand(4, source.getName()))
                return newValue;
            return null;
        }

        @Override
        public String description()
        {
            return "This setting can only be set by admins with op level 4";
        }
    }

    private static class HUDUpdateIntervalValidator extends Validator<Integer> {
        @Override
        public Integer validate(@Nullable CommandSource source, CarpetRule<Integer> changingRule, Integer newValue, String userInput) {
            if (newValue >=1 && newValue <= 2000) {
                return newValue;
            }
            return null;
        }
    }
}
