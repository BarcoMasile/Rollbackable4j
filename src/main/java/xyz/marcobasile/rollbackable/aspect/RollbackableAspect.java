package xyz.marcobasile.rollbackable.aspect;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import xyz.marcobasile.rollbackable.annotation.Action;
import xyz.marcobasile.rollbackable.annotation.Rollback;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Aspect
@Component
public class RollbackableAspect {

    @Pointcut(value = "@annotation(xyz.marcobasile.rollbackable.annotation.Action)")
    public void annotationPointcut() {}

    @SneakyThrows
    @Around(value = "annotationPointcut()")
    public Object around(ProceedingJoinPoint jp) {
        final Object obj = jp.getTarget();
        final Method actionMethod = anyWithAction(obj);

        final Optional<Method> rollbackMethodOpt = methodWithMatchinRollbackName(obj, actionMethod);
        if (rollbackMethodOpt.isEmpty()) {
            return proceed(jp, jp.getArgs());
        }

        final Method method = rollbackMethodOpt.get();
        try {
            return proceed(jp, jp.getArgs());
        } catch (Throwable ex){
            checkValidExceptionsOrRetrhow(forExceptions(actionMethod), ex);
            checkMethodParametersOrBust(method);
            method.setAccessible(true);
            return method.invoke(obj, ex);
        }
    }

    private void checkValidExceptionsOrRetrhow(Class<? extends Throwable>[] forExceptions, Throwable ex) throws Throwable {
        if (forExceptions.length == 0) {
            return;
        }

        for (Class<? extends Throwable> exc : forExceptions) {
            if (exc.isAssignableFrom(ex.getClass())) {
                return;
            }
        }

        throw ex;
    }

    private void checkMethodParametersOrBust(Method method) {
        for (Parameter parameter : method.getParameters()) {
            final Class<?> type = parameter.getType();
            if (Throwable.class.isAssignableFrom(type)) {
                return;
            }
        }

        throw new RuntimeException("Rollback method needs a Throwable parameter");
    }

    private Object proceed(ProceedingJoinPoint jp, Object[] args) throws Throwable {
        return args != null && args.length > 0 ? jp.proceed(args) : jp.proceed();
    }

    private Optional<Method> methodWithMatchinRollbackName(Object obj, Method actionMethod) {
        return anyWithRollback(obj, actionMethod.getAnnotation(Action.class).rollback());
    }

    private Optional<Method> anyWithRollback(Object obj, String name) {
        return Arrays.stream(obj.getClass().getDeclaredMethods())
                .filter(m -> m.getAnnotation(Rollback.class) != null)
                .filter(m -> m.getAnnotation(Rollback.class).name().equals(name))
                .findAny();
    }

    private Method anyWithAction(Object obj) {
        return Arrays.stream(obj.getClass().getDeclaredMethods())
                .filter(m -> m.getAnnotation(Action.class) != null)
                .findAny().get();
    }

    private Class<? extends Throwable>[] forExceptions(Method m) {
        return m.getAnnotation(Action.class).forExceptions();
    }
}