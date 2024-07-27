package carpet.api.settings;

import net.minecraft.text.Text;

import java.lang.annotation.*;
import java.util.List;

/**
 * Any field in this class annotated with this class is interpreted as a carpet rule.
 * The field must be static and have a type of one of:
 * - boolean
 * - int
 * - double
 * - String
 * - long
 * - float
 * - a subclass of Enum
 * The default value of the rule will be the initial value of the field.
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Rule {
    /**
     * The rule name, by default the same as the field name
     */
    String name() default ""; // default same as field name

    /**
     * A description of the rule
     */
    String desc();

    /**
     * Extra information about the rule
     */
    String[] extra() default {};

    /**
     * A list of categories the rule is in
     */
    String[] category();

    /**
     * Options to select in menu.
     * Inferred for booleans and enums. Otherwise, must be present.
     */
    String[] options() default {};

    /**
     * if a rule is not strict - can take any value, otherwise it needs to match
     * any of the options
     * For enums, its always strict, same for booleans - no need to set that for them.
     */
    boolean strict() default true;

    /**
     * If specified, the rule will automatically enable or disable
     * a builtin Scarpet Rule App with this name.
     */
    String appSource() default "";

    /**
     * The class of the validator checked when the rule is changed.
     */
    @SuppressWarnings("rawtypes")
    Class<? extends Validator>[] validators() default {};

    /**
     * The class of the condition checked when the rule is parsed, before being added
     * to the Settings Manager.
     */
    Class<? extends Condition>[] conditions() default {};

    /**
     * <p>Represents a condition that must be met for a rule to be registered in a {@link SettingsManager} via
     * {@link SettingsManager#parseSettingsClass(Class)}</p>
     *
     * @see Rule#conditions()
     * @see #shouldRegister()
     */
    interface Condition {
        /**
         * <p>Returns whether the rule that has had this {@link Condition} added should register.</p>
         *
         * @return {@code true} to register the rule, {@code false} otherwise
         */
        boolean shouldRegister();
    }
}
