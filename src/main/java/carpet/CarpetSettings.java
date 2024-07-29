package carpet;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.ParsedRule;
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
            options = {"ops", "2", "4"}
    )
    public static String carpetCommandPermissionLevel = "ops";

    // DEBUG for early dev, will remove later
    public static final String DEBUG = "Debug";
    @Rule(
            desc = "Boolean debug",
            category = DEBUG
    )
    public static boolean boolrule = false;

    @Rule(
            desc = "Int debug",
            category = DEBUG,
            options = {"1", "2", "3"}
    )
    public static int intrule = 1;

    @Rule(
            desc = "Double debug",
            category = DEBUG,
            options = {"6.9", "4.20"}
    )
    public static double doublerule = 6.9D;

    @Rule(
            desc = "String debug",
            category = DEBUG,
            options = {"abd", "def"}
    )
    public static String str = "aString";


}
