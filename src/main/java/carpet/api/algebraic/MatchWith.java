package carpet.api.algebraic;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MatchWith {
    public static enum Empty {
        EMPTY;
        @Override
		public String toString() { return ""; }
    }

    /**
     * @return The prefix required in matching. Stripped from the string argument as matched
     * You can use it on a Empty field to match this literal string. An Empty field matches for an
     * empty string, so matching a Empty with a literal prefix matches a literal string
     */
    String prefix() default "";
}
