package rs.kasuj;

import javax.inject.Qualifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Qualifier
@Target({ElementType.TYPE,ElementType.FIELD})
public @interface Testowy {
    Rodzaj aaa() default Rodzaj.AA;
    public enum Rodzaj{AA, BB, CC}
}
