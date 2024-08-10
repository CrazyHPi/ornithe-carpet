package carpet;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Rule;
import carpet.api.settings.Validator;
import carpet.utils.Messenger;
import carpet.utils.Translations;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.source.CommandSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;

import static carpet.api.settings.RuleCategory.*;

public class CarpetSettings {
    public static final String carpetVersion = FabricLoader.getInstance().getModContainer("carpet").orElseThrow(() -> new NoSuchElementException("No value present")).getMetadata().getVersion().toString();
    public static final Logger LOG = LogManager.getLogger("carpet");

    // carpet command related
    private static class LanguageValidator extends Validator<String> {
        @Override
        public String validate(CommandSource source, CarpetRule<String> currentRule, String newValue, String string) {
            if (!Translations.isValidLanguage(newValue)) {
                Messenger.m(source, "r " + newValue + " is not a valid language");
                return null;
            }
            CarpetSettings.language = newValue;
            Translations.updateLanguage();
            return newValue;
        }
    }
    @Rule(
            desc = "Sets the language for Carpet",
            category = FEATURE,
            options = {"en_us"},
            strict = true, // the current system doesn't handle fallbacks and other, not defined languages would make unreadable mess. Change later
            validators = LanguageValidator.class
    )
    public static String language = "en_us";

    @Rule(desc = "Gbhs sgnf sadsgras fhskdpri!!!", category = EXPERIMENTAL)
    public static boolean superSecretSetting = false;

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
    @Rule(
            desc = "Carpet command permission level. Can only be set via .conf file",
            category = CREATIVE,
            validators = CarpetPermissionLevel.class,
            options = {"ops", "0", "2", "4"}
    )
    public static String carpetCommandPermissionLevel = "ops";

    // ================ //
    // ==== BUGFIX ==== //
    // ================ //

    @Rule(desc = "Fixes the elytra check similar to 1.15 where the player do not have to fall to deploy elytra anymore.", category = BUGFIX)
    public static boolean elytraCheckFix;

    // ================== //
    // ==== SURVIVAL ==== //
    // ================== //

    @Rule(desc = "Prevents players from rubberbanding when moving too fast", category = SURVIVAL)
    public static boolean antiCheatSpeed = false;

    // ================== //
    // ==== CREATIVE ==== //
    // ================== //

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
            category = {CREATIVE, CLIENT}
    )
    public static boolean creativeNoClip = false;

    @Rule(
            desc = "hoppers pointing to wool will count items passing through them",
            extra = {
                    "Enables /counter command, and actions while placing red and green carpets on wool blocks",
                    "Use /counter <color?> reset to reset the counter, and /counter <color?> to query",
                    "In survival, place green carpet on same color wool to query, red to reset the counters",
                    "Counters are global and shared between players, 16 channels available",
                    "Items counted are destroyed, count up to one stack per tick per hopper"
            },
            category = {COMMAND, CREATIVE, FEATURE}
    )
    public static boolean hopperCounters = false;

    @Rule(desc = "Items thrown into a cactus will count items that are destroyed in them.", category = {CREATIVE})
    public static boolean cactusCounter = false;

    // ====================== //
    // ==== EXPERIMENTAL ==== //
    // ====================== //


    // ====================== //
    // ==== OPTIMIZATION ==== //
    // ====================== //


    // ================= //
    // ==== FEATURE ==== //
    // ================= //

    private static class HUDUpdateIntervalValidator extends Validator<Integer> {
        @Override
        public Integer validate(@Nullable CommandSource source, CarpetRule<Integer> changingRule, Integer newValue, String userInput) {
            if (newValue >=1 && newValue <= 2000) {
                return newValue;
            }
            return null;
        }
    }
    @Rule(
            desc = "HUD update interval",
            category = FEATURE,
            options = {"1", "5", "20", "100"},
            validators = HUDUpdateIntervalValidator.class
    )
    public static int HUDUpdateInterval = 20;

    // ================= //
    // ==== COMMAND ==== //
    // ================= //

    @Rule(desc = "Enables /log command to monitor events via chat and overlays", category = COMMAND)
    public static String commandLog = "true";

    // ============= //
    // ==== TNT ==== //
    // ============= //

    @Rule(desc = "Explosions won't destroy blocks", category = TNT)
    public static boolean explosionNoBlockDamage = false;

}
