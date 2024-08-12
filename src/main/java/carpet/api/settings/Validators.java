package carpet.api.settings;

import carpet.CarpetSettings;
import carpet.utils.Messenger;
import carpet.utils.Translations;
import net.minecraft.server.command.source.CommandSource;

import java.util.Arrays;
import java.util.List;

/**
 * <p>A collection of standard {@link Validator validators} you can use in your rules.</p>
 *
 * @see Rule
 * @see Rule#validators()
 */
public final class Validators {
    private Validators() {
    }

    /**
     * <p>A {@link Validator} that checks whether the given {@link String} value was a valid command level as Carpet allows them,
     * so either a number from 0 to 4, or one of the keywords {@code true}, {@code false} or {@code ops} </p>
     *
     * <p>While there is no public API method for checking whether a source can execute a command,
     * {@link CommandHelper#canUseCommand(CommandSourceStack, Object)} is not expected to change anytime soon.</p>
     */
    public static class CommandLevel extends Validator<String> {

        public static final List<String> OPTIONS = Arrays.asList("true", "false", "ops", "0", "1", "2", "3", "4");

        @Override
        public String validate(CommandSource source, CarpetRule<String> currentRule, String newValue, String userString) {
            if (!OPTIONS.contains(newValue)) {
                return null;
            }
            return newValue;
        }

        @Override
        public String description() {
            return "Can be limited to 'ops' only, true/false for everyone/no one, or a custom permission level";
        }
    }

    /**
     * <p>A {@link Validator} that checks whether the entered number is equal or greater than {@code 0}.</p>
     */
    public static class NonNegativeNumber<T extends Number> extends Validator<T> {
        @Override
        public T validate(CommandSource source, CarpetRule<T> currentRule, T newValue, String string) {
            return newValue.doubleValue() >= 0 ? newValue : null;
        }

        @Override
        public String description() {
            return "Must be a positive number or 0";
        }
    }

    /**
     * <p>A {@link Validator} that checks whether the entered number is between 0 and 1, inclusive.</p>
     */
    public static class Probablity<T extends Number> extends Validator<T> {
        @Override
        public T validate(CommandSource source, CarpetRule<T> currentRule, T newValue, String string) {
            return (newValue.doubleValue() >= 0 && newValue.doubleValue() <= 1) ? newValue : null;
        }

        @Override
        public String description() {
            return "Must be between 0 and 1";
        }
    }

    public static class StrictValidator<T> extends Validator<T>
    {
        @Override
        public T validate(CommandSource source, CarpetRule<T> currentRule, T newValue, String string)
        {
            if (!currentRule.suggestions().contains(string))
            {
                Messenger.m(source, "r Valid options: " + currentRule.suggestions().toString());
                return null;
            }
            return newValue;
        }
    }

    public static class CarpetPermissionLevel extends Validator<String> {
        @Override
        //#if MC>=11300
//$$ 		public String validate(CommandSourceStack source, CarpetRule<String> currentRule, String newValue, String string) {
        //#else
        public String validate(CommandSource source, CarpetRule<String> currentRule, String newValue, String string) {
        //#endif
            //#if MC>=11300
//$$ 			if (source == null || source.hasPermissions(4)) {
            //#else
            if (source == null || source.canUseCommand(4, source.getName())) {
            //#endif
                return newValue;
            }
            return null;
        }

        @Override
        public String description() {
            return "This setting can only be set by admins with op level 4";
        }
    }

    public static class OneHourMaxDelayLimit extends Validator<Integer> {
        @Override
        //#if MC>=11300
//$$ 		public Integer validate(CommandSourceStack source, CarpetRule<Integer> currentRule, Integer newValue, String string) {
        //#else
        public Integer validate(CommandSource source, CarpetRule<Integer> currentRule, Integer newValue, String string) {
        //#endif
            return (newValue > 0 && newValue <= 72000) ? newValue : null;
        }

        @Override
        public String description() {
            return "You must choose a value from 1 to 72000";
        }
    }

    public static class Percentage extends Validator<Integer> {
        @Override
        public Integer validate(CommandSource source, CarpetRule<Integer> changingRule, Integer newValue, String userInput) {
            return (newValue >= 0 && newValue <= 100) ? newValue : null;
        }

        public String description() {
            return "You must choose a value from 0 to 100";
        }
    }

    public static class PositiveIn10Bits extends Validator<Integer> {
        @Override
        //#if MC>=11300
//$$ 		public Integer validate(CommandSourceStack source, CarpetRule<Integer> currentRule, Integer newValue, String string) {
        //#else
        public Integer validate(CommandSource source, CarpetRule<Integer> currentRule, Integer newValue, String string) {
        //#endif
            return (newValue > 0 && newValue <= 1024) ? newValue : null;
        }

        @Override
        public String description() { return "You must choose a value from 1 to 1024";}
    }

    public static class LanguageValidator extends Validator<String> {
        @Override
        //#if MC>=11300
//$$ 		public String validate(CommandSourceStack source, CarpetRule<String> currentRule, String newValue, String string) {
        //#else
        public String validate(CommandSource source, CarpetRule<String> currentRule, String newValue, String string) {
        //#endif
            CarpetSettings.language = newValue;
            Translations.updateLanguage();
            return newValue;
        }
    }

    public static class Probability<T extends Number> extends Validator<T> {
        @Override
        //#if MC>=11300
    //$$ 		public T validate(CommandSourceStack source, CarpetRule<T> currentRule, T newValue, String string) {
        //#else
        public T validate(CommandSource source, CarpetRule<T> currentRule, T newValue, String string) {
            //#endif
            return (newValue.doubleValue() >= 0 && newValue.doubleValue() <= 1) ? newValue : null;
        }

        @Override
        public String description() {
            return "Must be between 0 and 1";
        }
    }

    public static class OptionalProbability<T extends Number> extends Validator<T> {
        @Override
        //#if MC>=11300
        //$$ 		public T validate(CommandSourceStack source, CarpetRule<T> currentRule, T newValue, String string) {
        //#else
        public T validate(CommandSource source, CarpetRule<T> currentRule, T newValue, String string) {
            //#endif
            // <0 = default
            return newValue.doubleValue() <= 1 ? newValue : null;
        }

        @Override
        public String description() {
            return "Must be between 0 and 1";
        }
    }

    public static abstract class SideEffectValidator<T> extends Validator<T> {
        public abstract T parseValue(T newValue);

        public abstract void performEffect(T newValue);

        @Override
        public T validate(CommandSource source, CarpetRule<T> changingRule, T newValue, String userInput) {
            performEffect(newValue);
            return parseValue(newValue);
        }
    }
}
