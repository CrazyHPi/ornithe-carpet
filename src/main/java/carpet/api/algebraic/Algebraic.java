package carpet.api.algebraic;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Algebraic {
    /**
     * @return array of constructor classes for this algebraic data type
     */
    Class<?>[] value();
}
