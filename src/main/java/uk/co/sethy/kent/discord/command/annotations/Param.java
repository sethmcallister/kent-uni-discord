package uk.co.sethy.kent.discord.command.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
    String name();

    boolean wildcard() default false;

    String defaultValue() default "";
}
