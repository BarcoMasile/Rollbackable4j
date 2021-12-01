package xyz.marcobasile.rollbackable.annotation;

import java.lang.annotation.*;

@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Action {

    String rollback() default "";

    Class<? extends Throwable>[] forExceptions() default {};
}
