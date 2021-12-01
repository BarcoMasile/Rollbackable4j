package xyz.marcobasile.rollbackable.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Rollback {

    String name() default "";
}
